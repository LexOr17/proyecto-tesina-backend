package com.upemor.app_nutriologo.reportes.service;

import com.upemor.app_nutriologo.medidas_antropometricas.entity.MedidaAntropometrica;
import com.upemor.app_nutriologo.medidas_antropometricas.repository.MedidaAntropometricaRepository;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.reportes.dto.ReporteDemograficoDTO;
import com.upemor.app_nutriologo.reportes.dto.ReporteProgresoDTO;
import com.upemor.app_nutriologo.usuario.enums.Genero;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final PacienteRepository pacienteRepository;
    private final MedidaAntropometricaRepository medidaRepository;

    @Transactional(readOnly = true)
    public ReporteDemograficoDTO generarReporteDemografico() {
        List<Paciente> pacientes = pacienteRepository.findAll();
        
        long total = pacientes.size();
        
        Map<String, Long> porNutriologo = pacientes.stream()
                .filter(p -> p.getNutriologo() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getNutriologo().getNombre() + " " + p.getNutriologo().getApellidoPaterno(),
                        Collectors.counting()
                ));
                
        Map<String, Long> porGenero = pacientes.stream()
                .filter(p -> p.getGenero() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getGenero().name(),
                        Collectors.counting()
                ));
                
        Map<String, Long> porEdad = pacientes.stream()
                .filter(p -> p.getFechaNacimiento() != null)
                .collect(Collectors.groupingBy(
                        p -> calcularRangoEdad(p.getFechaNacimiento()),
                        Collectors.counting()
                ));

        return ReporteDemograficoDTO.builder()
                .totalPacientes(total)
                .distribucionPorNutriologo(porNutriologo)
                .distribucionPorGenero(porGenero)
                .distribucionPorRangoEdad(porEdad)
                .build();
    }

    @Transactional(readOnly = true)
    public ReporteProgresoDTO generarReporteProgreso(Long pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + pacienteId));
                
        List<MedidaAntropometrica> medidas = medidaRepository.findByPacienteIdOrderByFechaMedicionAsc(pacienteId);
        
        List<LocalDate> fechas = new ArrayList<>();
        List<BigDecimal> pesos = new ArrayList<>();
        List<BigDecimal> porcentajesGrasa = new ArrayList<>();
        List<BigDecimal> indicesCinturaTalla = new ArrayList<>();
        List<BigDecimal> indicesCinturaCadera = new ArrayList<>();
        
        for (MedidaAntropometrica m : medidas) {
            fechas.add(m.getFechaMedicion());
            pesos.add(m.getPeso());
            
            // Cálculos dinámicos
            BigDecimal pctGrasa = calcularPorcentajeGrasa(m, paciente.getGenero());
            porcentajesGrasa.add(pctGrasa);
            
            BigDecimal ict = calcularIndiceCinturaTalla(m);
            indicesCinturaTalla.add(ict);
            
            BigDecimal icc = calcularIndiceCinturaCadera(m);
            indicesCinturaCadera.add(icc);
        }
        
        return ReporteProgresoDTO.builder()
                .fechas(fechas)
                .pesos(pesos)
                .porcentajesGrasa(porcentajesGrasa)
                .indicesCinturaTalla(indicesCinturaTalla)
                .indicesCinturaCadera(indicesCinturaCadera)
                .build();
    }

    private String calcularRangoEdad(LocalDate fechaNacimiento) {
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < 18) return "Menores de 18";
        if (edad <= 25) return "18-25";
        if (edad <= 35) return "26-35";
        if (edad <= 45) return "36-45";
        if (edad <= 60) return "46-60";
        return "Mayores de 60";
    }

    private BigDecimal calcularPorcentajeGrasa(MedidaAntropometrica m, Genero genero) {
        if (m.getCintura() == null || m.getCuello() == null || m.getAltura() == null) {
            return null;
        }

        double cintura = m.getCintura().doubleValue();
        double cuello = m.getCuello().doubleValue();
        double altura = m.getAltura().doubleValue();
        
        double grasa = 0.0;
        
        if (genero == Genero.MASCULINO) {
            // Formula US Navy Hombres: 495 / (1.0324 - 0.19077 * log10(cintura - cuello) + 0.15456 * log10(altura)) - 450
            if (cintura - cuello > 0) {
                double den = 1.0324 - 0.19077 * Math.log10(cintura - cuello) + 0.15456 * Math.log10(altura);
                if (den != 0) {
                    grasa = (495.0 / den) - 450.0;
                }
            }
        } else if (genero == Genero.FEMENINO) {
            // Formula US Navy Mujeres: 495 / (1.29579 - 0.35004 * log10(cintura + cadera - cuello) + 0.22100 * log10(altura)) - 450
            if (m.getCadera() != null) {
                double cadera = m.getCadera().doubleValue();
                if (cintura + cadera - cuello > 0) {
                    double den = 1.29579 - 0.35004 * Math.log10(cintura + cadera - cuello) + 0.22100 * Math.log10(altura);
                    if (den != 0) {
                        grasa = (495.0 / den) - 450.0;
                    }
                }
            }
        }
        
        if (grasa <= 0 || grasa > 100) {
            return null;
        }
        
        return BigDecimal.valueOf(grasa).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularIndiceCinturaTalla(MedidaAntropometrica m) {
        if (m.getCintura() == null || m.getAltura() == null || m.getAltura().compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        // ICC = cintura / altura
        return m.getCintura().divide(m.getAltura(), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularIndiceCinturaCadera(MedidaAntropometrica m) {
        if (m.getCintura() == null || m.getCadera() == null || m.getCadera().compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return m.getCintura().divide(m.getCadera(), 2, RoundingMode.HALF_UP);
    }
}
