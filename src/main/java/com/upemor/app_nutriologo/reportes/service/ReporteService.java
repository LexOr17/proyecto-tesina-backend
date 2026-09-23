package com.upemor.app_nutriologo.reportes.service;

import com.upemor.app_nutriologo.alimento.repository.AlimentoRepository;
import com.upemor.app_nutriologo.citas.entity.Cita;
import com.upemor.app_nutriologo.citas.enums.Estado;
import com.upemor.app_nutriologo.citas.repository.CitaRepository;
import com.upemor.app_nutriologo.common.exceptions.BadRequestException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.diario_comidas.entity.DiarioComida;
import com.upemor.app_nutriologo.diario_comidas.enums.EstadoRegistro;
import com.upemor.app_nutriologo.diario_comidas.repository.DiarioComidaRepository;
import com.upemor.app_nutriologo.medidas_antropometricas.entity.MedidaAntropometrica;
import com.upemor.app_nutriologo.medidas_antropometricas.repository.MedidaAntropometricaRepository;
import com.upemor.app_nutriologo.meta.enums.EstadoMeta;
import com.upemor.app_nutriologo.meta.repository.MetaRepository;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.nutriologo.repository.NutriologoRepository;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import com.upemor.app_nutriologo.plantillas.repository.PlantillaRepository;
import com.upemor.app_nutriologo.reportes.dto.*;
import com.upemor.app_nutriologo.security.UserContextService;
import com.upemor.app_nutriologo.sugerencia_plantilla.entity.SugerenciaPlantilla;
import com.upemor.app_nutriologo.sugerencia_plantilla.entity.SugerenciaPlantillaDetalle;
import com.upemor.app_nutriologo.sugerencia_plantilla.enums.CategoriaMotivoSugerencia;
import com.upemor.app_nutriologo.sugerencia_plantilla.enums.EstadoSugerencia;
import com.upemor.app_nutriologo.sugerencia_plantilla.repository.SugerenciaPlantillaRepository;
import com.upemor.app_nutriologo.usuario.enums.Genero;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final PacienteRepository pacienteRepository;
    private final MedidaAntropometricaRepository medidaRepository;
    private final DiarioComidaRepository diarioComidaRepository;
    private final MetaRepository metaRepository;
    private final UserContextService userContextService;
    private final CitaRepository citaRepository;
    private final NutriologoRepository nutriologoRepository;
    private final AlimentoRepository alimentoRepository;
    private final PlantillaRepository plantillaRepository;
    private final SugerenciaPlantillaRepository sugerenciaRepository;

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
        Long currentUserId = userContextService.getCurrentUserId();
        var roles = userContextService.getCurrentUserRoles();
        if ((roles.contains("ROLE_PACIENTE") || roles.contains("PACIENTE")) && !currentUserId.equals(pacienteId)) {
            throw new AccessDeniedException("No tienes permiso para ver el progreso de otro paciente.");
        }

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
            BigDecimal pctGrasa = calcularPorcentajeGrasa(m, paciente.getGenero(), paciente.getFechaNacimiento());
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

    @Transactional(readOnly = true)
    public ReporteSeguimientoPlanDTO generarReporteSeguimientoPlan(Long pacienteId, LocalDate inicio, LocalDate fin) {
        Long currentUserId = userContextService.getCurrentUserId();
        var roles = userContextService.getCurrentUserRoles();
        if ((roles.contains("ROLE_PACIENTE") || roles.contains("PACIENTE")) && !currentUserId.equals(pacienteId)) {
            throw new AccessDeniedException("No tienes permiso para ver el reporte de otro paciente.");
        }

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + pacienteId));

        validarRangoFechas(inicio, fin);
        LocalDate fechaFin = (fin != null) ? fin : LocalDate.now();
        LocalDate fechaInicio = (inicio != null) ? inicio : fechaFin.minusDays(30);
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("Rango de fechas inválido: La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        // 1. Consultar registros del diario en el período
        List<DiarioComida> diarios = diarioComidaRepository
                .findByPacienteIdAndFechaBetweenOrderByFechaAsc(pacienteId, fechaInicio, fechaFin);

        long cumplidos = 0;
        long modificados = 0;
        long saltados = 0;
        long noRegistrados = 0;

        // Gráfica de días de desviación: Inicializar todos los días de la semana
        Map<String, Long> desviacionPorDia = new LinkedHashMap<>();
        desviacionPorDia.put("Lunes", 0L);
        desviacionPorDia.put("Martes", 0L);
        desviacionPorDia.put("Miércoles", 0L);
        desviacionPorDia.put("Jueves", 0L);
        desviacionPorDia.put("Viernes", 0L);
        desviacionPorDia.put("Sábado", 0L);
        desviacionPorDia.put("Domingo", 0L);

        for (DiarioComida d : diarios) {
            if (d.getEstado() == EstadoRegistro.CUMPLIDO) {
                cumplidos++;
            } else if (d.getEstado() == EstadoRegistro.MODIFICADO) {
                modificados++;
                String dia = traducirDiaSemana(d.getFecha().getDayOfWeek());
                desviacionPorDia.put(dia, desviacionPorDia.getOrDefault(dia, 0L) + 1);
            } else if (d.getEstado() == EstadoRegistro.SALTADA) {
                saltados++;
                String dia = traducirDiaSemana(d.getFecha().getDayOfWeek());
                desviacionPorDia.put(dia, desviacionPorDia.getOrDefault(dia, 0L) + 1);
            } else if (d.getEstado() == EstadoRegistro.NO_REGISTRADO) {
                noRegistrados++;
            }
        }

        long totalRegistros = diarios.size();
        long totalEvaluados = cumplidos + modificados + saltados;
        double porcentajeApego = 0.0;
        if (totalEvaluados > 0) {
            porcentajeApego = ((double) cumplidos / totalEvaluados) * 100.0;
            porcentajeApego = Math.round(porcentajeApego * 10.0) / 10.0;
        }

        // 2. Metas del paciente
        List<Object[]> statusCounts = metaRepository.countMetasByStatusForPaciente(pacienteId);
        long metasCumplidas = 0;
        long metasEnProgreso = 0;
        long metasNoCumplidas = 0;
        long totalMetas = 0;

        for (Object[] row : statusCounts) {
            EstadoMeta estado = (EstadoMeta) row[0];
            Long count = (Long) row[1];
            totalMetas += count;

            if (estado == EstadoMeta.CUMPLIDA) {
                metasCumplidas += count;
            } else if (estado == EstadoMeta.PENDIENTE || estado == EstadoMeta.POR_VALIDAR) {
                metasEnProgreso += count;
            } else if (estado == EstadoMeta.RECHAZADA || estado == EstadoMeta.NO_CUMPLIDA) {
                metasNoCumplidas += count;
            }
        }

        double porcentajeCumplimientoMetas = 0.0;
        if (totalMetas > 0) {
            porcentajeCumplimientoMetas = ((double) metasCumplidas / totalMetas) * 100.0;
            porcentajeCumplimientoMetas = Math.round(porcentajeCumplimientoMetas * 10.0) / 10.0;
        }

        String nombreCompleto = paciente.getNombre() + " " + paciente.getApellidoPaterno() + 
                (paciente.getApellidoMaterno() != null ? " " + paciente.getApellidoMaterno() : "");

        return ReporteSeguimientoPlanDTO.builder()
                .totalMetas(totalMetas)
                .metasCumplidas(metasCumplidas)
                .metasEnProgreso(metasEnProgreso)
                .metasNoCumplidas(metasNoCumplidas)
                .porcentajeCumplimientoMetas(porcentajeCumplimientoMetas)
                .totalRegistros(totalRegistros)
                .registrosCumplidos(cumplidos)
                .registrosModificados(modificados)
                .registrosSaltados(saltados)
                .registrosNoRegistrados(noRegistrados)
                .porcentajeApego(porcentajeApego)
                .desviacionPorDia(desviacionPorDia)
                .periodoInicio(fechaInicio)
                .periodoFin(fechaFin)
                .nombrePaciente(nombreCompleto.trim())
                .build();
    }

    private String traducirDiaSemana(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    private String calcularRangoEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return "No especificado";
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        if (edad < 18) return "Menores de 18";
        if (edad <= 25) return "18-25";
        if (edad <= 35) return "26-35";
        if (edad <= 45) return "36-45";
        if (edad <= 60) return "46-60";
        return "Mayores de 60";
    }

    private BigDecimal calcularPorcentajeGrasa(MedidaAntropometrica m, Genero genero, LocalDate fechaNacimiento) {
        if (m.getPeso() == null || m.getAltura() == null || m.getAltura().compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        double peso = m.getPeso().doubleValue();
        double rawAltura = m.getAltura().doubleValue();
        if (rawAltura <= 0 || peso <= 0) return null;

        // Normalizar altura a centímetros (cm) y metros (m)
        double alturaCm = rawAltura < 3.0 ? rawAltura * 100.0 : rawAltura;
        double alturaM = rawAltura >= 3.0 ? rawAltura / 100.0 : rawAltura;

        double grasa = 0.0;
        boolean navyCalculado = false;

        // 1. Intentar con fórmula de la Marina de EE. UU. (US Navy) si perímetros requeridos están presentes
        if (m.getCintura() != null && m.getCuello() != null) {
            double cintura = m.getCintura().doubleValue();
            double cuello = m.getCuello().doubleValue();

            if (genero == Genero.MASCULINO) {
                if (cintura - cuello > 0) {
                    double den = 1.0324 - 0.19077 * Math.log10(cintura - cuello) + 0.15456 * Math.log10(alturaCm);
                    if (den > 0) {
                        double g = (495.0 / den) - 450.0;
                        if (g > 3.0 && g < 70.0) {
                            grasa = g;
                            navyCalculado = true;
                        }
                    }
                }
            } else if (genero == Genero.FEMENINO && m.getCadera() != null) {
                double cadera = m.getCadera().doubleValue();
                if (cintura + cadera - cuello > 0) {
                    double den = 1.29579 - 0.35004 * Math.log10(cintura + cadera - cuello) + 0.22100 * Math.log10(alturaCm);
                    if (den > 0) {
                        double g = (495.0 / den) - 450.0;
                        if (g > 3.0 && g < 70.0) {
                            grasa = g;
                            navyCalculado = true;
                        }
                    }
                }
            }
        }

        // 2. Si no se cuentan con perímetros o Navy no aplica, usar Fórmula Clínica de Deurenberg (basada en IMC, Edad y Género)
        if (!navyCalculado) {
            double imc = peso / (alturaM * alturaM);
            int edad = fechaNacimiento != null ? Period.between(fechaNacimiento, LocalDate.now()).getYears() : 30;
            int sexo = (genero == Genero.MASCULINO) ? 1 : 0;

            if (edad >= 16) {
                // Adultos: % Grasa = (1.20 * IMC) + (0.23 * edad) - (10.8 * sexo) - 5.4
                grasa = (1.20 * imc) + (0.23 * edad) - (10.8 * sexo) - 5.4;
            } else {
                // Niños/adolescentes: % Grasa = (1.51 * IMC) - (0.70 * edad) - (3.6 * sexo) + 1.4
                grasa = (1.51 * imc) - (0.70 * edad) - (3.6 * sexo) + 1.4;
            }
        }

        if (grasa <= 2.0 || grasa > 75.0) {
            return null;
        }

        return BigDecimal.valueOf(grasa).setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularIndiceCinturaTalla(MedidaAntropometrica m) {
        if (m.getCintura() == null || m.getAltura() == null || m.getAltura().compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        double cintura = m.getCintura().doubleValue();
        double rawAltura = m.getAltura().doubleValue();
        double alturaCm = rawAltura < 3.0 ? rawAltura * 100.0 : rawAltura;

        double ict = cintura / alturaCm;
        return BigDecimal.valueOf(ict).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularIndiceCinturaCadera(MedidaAntropometrica m) {
        if (m.getCintura() == null || m.getCadera() == null || m.getCadera().compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return m.getCintura().divide(m.getCadera(), 2, RoundingMode.HALF_UP);
    }

    // ─── Reporte de Asistencia y Demanda de Servicios ─────────────────────────

    @Transactional(readOnly = true)
    public ReporteAsistenciaDTO generarReporteAsistencia(Long nutriologoId, LocalDate inicio, LocalDate fin) {

        validarRangoFechas(inicio, fin);
        LocalDate fechaFin   = (fin   != null) ? fin   : LocalDate.now();
        LocalDate fechaInicio = (inicio != null) ? inicio : fechaFin.minusDays(29);
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("Rango de fechas inválido: La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        LocalDateTime dtInicio = fechaInicio.atStartOfDay();
        LocalDateTime dtFin    = fechaFin.atTime(23, 59, 59);

        boolean esAdmin = userContextService.isAdmin();

        List<Cita> citas;
        String nombreNutriologo = null;

        if (nutriologoId != null) {
            if (!esAdmin) {
                Long currentId = userContextService.getCurrentUserId();
                if (!currentId.equals(nutriologoId)) {
                    throw new AccessDeniedException(
                            "Solo puedes consultar tu propio reporte de asistencia.");
                }
            }
            citas = citaRepository.findCitasParaReporteAsistenciaPorNutriologo(nutriologoId, dtInicio, dtFin);
            Nutriologo n = nutriologoRepository.findById(nutriologoId).orElse(null);
            if (n != null) {
                nombreNutriologo = n.getNombre() + " " + n.getApellidoPaterno();
            }
        } else if (esAdmin) {
            citas = citaRepository.findCitasParaReporteAsistenciaGlobal(dtInicio, dtFin);
        } else {
            Long currentId = userContextService.getCurrentUserId();
            citas = citaRepository.findCitasParaReporteAsistenciaPorNutriologo(currentId, dtInicio, dtFin);
            Nutriologo n = nutriologoRepository.findById(currentId).orElse(null);
            if (n != null) {
                nombreNutriologo = n.getNombre() + " " + n.getApellidoPaterno();
            }
        }

        long asistidas    = citas.stream().filter(c -> c.getEstado() == Estado.ASISTIO).count();
        long noAsistidas  = citas.stream().filter(c -> c.getEstado() == Estado.NO_ASISTIO).count();
        long canceladas   = citas.stream().filter(c ->
                c.getEstado() == Estado.CANCELADA || c.getEstado() == Estado.CANCELACION_SOLICITADA).count();
        long programadas  = citas.stream().filter(c ->
                c.getEstado() == Estado.PROGRAMADA || c.getEstado() == Estado.SOLICITADA).count();
        long reprogramadas = citas.stream().filter(c ->
                c.getEstado() == Estado.REPROGRAMACION_SOLICITADA).count();
        long total = citas.size();

        long baseAsistencia = asistidas + noAsistidas;
        double pctAsistencia   = baseAsistencia > 0
                ? Math.round(((double) asistidas  / baseAsistencia) * 1000.0) / 10.0 : 0.0;
        double pctInasistencia = baseAsistencia > 0
                ? Math.round(((double) noAsistidas / baseAsistencia) * 1000.0) / 10.0 : 0.0;
        double pctCancelacion  = total > 0
                ? Math.round(((double) canceladas  / total)         * 1000.0) / 10.0 : 0.0;

        Map<String, Long> demandaPorMotivo = citas.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getMotivoCita() != null ? c.getMotivoCita().name() : "OTRO",
                        Collectors.counting()
                ));

        String motivoFrecuente = demandaPorMotivo.entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("N/A");

        Map<String, Long> demandaOrdenada = demandaPorMotivo.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return ReporteAsistenciaDTO.builder()
                .totalCitas(total)
                .citasAsistidas(asistidas)
                .citasNoAsistidas(noAsistidas)
                .citasCanceladas(canceladas)
                .citasProgramadas(programadas)
                .citasReprogramadas(reprogramadas)
                .porcentajeAsistencia(pctAsistencia)
                .porcentajeInasistencia(pctInasistencia)
                .porcentajeCancelacion(pctCancelacion)
                .demandaPorMotivo(demandaOrdenada)
                .motivoMasFrecuente(motivoFrecuente)
                .periodoInicio(fechaInicio)
                .periodoFin(fechaFin)
                .nombreNutriologo(nombreNutriologo)
                .build();
    }

    // ─── Reporte de Desempeño y Atención del Nutriólogo ──────────────────────

    @Transactional(readOnly = true)
    public ReporteDesempenoNutriologoDTO generarReporteDesempenoNutriologo(Long nutriologoId, LocalDate inicio, LocalDate fin) {
        validarRangoFechas(inicio, fin);
        LocalDate fechaFin    = (fin    != null) ? fin    : LocalDate.now();
        LocalDate fechaInicio = (inicio != null) ? inicio : fechaFin.minusDays(29);
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("Rango de fechas inválido: La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        LocalDateTime dtInicio = fechaInicio.atStartOfDay();
        LocalDateTime dtFin    = fechaFin.atTime(23, 59, 59);

        boolean esAdmin = userContextService.isAdmin();
        Long targetNutriologoId = null;
        String nombreNutriologo = null;
        String correoNutriologo = null;

        long totalPacientesActivos;
        List<Cita> citas;
        List<Object[]> topAlimentosRaw;
        long totalAlimentosRegistrados;
        List<Object[]> alimentosPorCatRaw;
        long totalPlantillasRegistradas;
        List<Object[]> plantillasPorTipoRaw;

        if (nutriologoId != null) {
            if (!esAdmin) {
                Long currentId = userContextService.getCurrentUserId();
                if (!currentId.equals(nutriologoId)) {
                    throw new AccessDeniedException("Solo puedes consultar tu propio reporte de desempeño.");
                }
            }
            targetNutriologoId = nutriologoId;
        } else if (!esAdmin) {
            targetNutriologoId = userContextService.getCurrentUserId();
        }

        if (targetNutriologoId != null) {
            Nutriologo n = nutriologoRepository.findById(targetNutriologoId).orElse(null);
            if (n != null) {
                nombreNutriologo = n.getNombre() + " " + n.getApellidoPaterno() + (n.getApellidoMaterno() != null ? " " + n.getApellidoMaterno() : "");
                correoNutriologo = n.getCorreo();
            }
            totalPacientesActivos = pacienteRepository.countPacientesActivosByNutriologoId(targetNutriologoId);
            citas = citaRepository.findCitasParaReporteAsistenciaPorNutriologo(targetNutriologoId, dtInicio, dtFin);
            topAlimentosRaw = plantillaRepository.findTopAlimentosMasAsignadosPorNutriologo(targetNutriologoId);
            totalAlimentosRegistrados = alimentoRepository.countByRegistradoPorId(targetNutriologoId);
            alimentosPorCatRaw = alimentoRepository.countAlimentosPorCategoriaByRegistradoPorId(targetNutriologoId);
            totalPlantillasRegistradas = plantillaRepository.countByUsuarioId(targetNutriologoId);
            plantillasPorTipoRaw = plantillaRepository.countPlantillasPorTipoComidaByUsuarioId(targetNutriologoId);
        } else {
            totalPacientesActivos = pacienteRepository.countPacientesActivosTotal();
            citas = citaRepository.findCitasParaReporteAsistenciaGlobal(dtInicio, dtFin);
            topAlimentosRaw = plantillaRepository.findTopAlimentosMasAsignadosGlobal();
            totalAlimentosRegistrados = alimentoRepository.count();
            alimentosPorCatRaw = alimentoRepository.countAlimentosPorCategoriaGlobal();
            totalPlantillasRegistradas = plantillaRepository.count();
            plantillasPorTipoRaw = plantillaRepository.countPlantillasPorTipoComidaGlobal();
        }

        long totalCitasPeriodo = citas.size();
        long citasConcretadas = citas.stream().filter(c -> c.getEstado() == Estado.ASISTIO).count();
        long citasCanceladas = citas.stream().filter(c ->
                c.getEstado() == Estado.CANCELADA || c.getEstado() == Estado.CANCELACION_SOLICITADA).count();
        long citasPendientes = citas.stream().filter(c ->
                c.getEstado() == Estado.PROGRAMADA || c.getEstado() == Estado.SOLICITADA).count();
        long citasNoAsistidas = citas.stream().filter(c -> c.getEstado() == Estado.NO_ASISTIO).count();

        double porcentajeAtencion = totalCitasPeriodo > 0
                ? Math.round(((double) citasConcretadas / totalCitasPeriodo) * 1000.0) / 10.0 : 0.0;
        double porcentajeCancelacion = totalCitasPeriodo > 0
                ? Math.round(((double) citasCanceladas / totalCitasPeriodo) * 1000.0) / 10.0 : 0.0;
        double promedioCitasPorPaciente = totalPacientesActivos > 0
                ? Math.round(((double) citasConcretadas / totalPacientesActivos) * 100.0) / 100.0 : 0.0;

        String nivelCargaTrabajo;
        String diagnosticoCarga;
        if (citasConcretadas >= 50 || (totalPacientesActivos > 30 && citasConcretadas >= 40)) {
            nivelCargaTrabajo = "SOBRECARGA";
            diagnosticoCarga = "Alta saturación clínica en el periodo. Se sugiere balancear la asignación de consultas para mantener la calidad de atención.";
        } else if (citasConcretadas >= 30 || totalPacientesActivos >= 20) {
            nivelCargaTrabajo = "ALTA";
            diagnosticoCarga = "Carga de trabajo elevada y consistente con ritmo asistencial alto. Mantener monitoreo de agenda.";
        } else if (citasConcretadas >= 15 || totalPacientesActivos >= 10) {
            nivelCargaTrabajo = "OPTIMA";
            diagnosticoCarga = "Carga clínica equilibrada. Excelente desempeño en proporción de pacientes activos y consultas asistidas.";
        } else if (citasConcretadas >= 5 || totalPacientesActivos >= 5) {
            nivelCargaTrabajo = "MODERADA";
            diagnosticoCarga = "Carga moderada con disponibilidad abierta para agendar nuevas consultas o incorporar pacientes.";
        } else {
            nivelCargaTrabajo = "BAJA";
            diagnosticoCarga = "Baja actividad clínica registrada en el periodo. Recomendado incentivar el seguimiento y citas de control.";
        }

        long totalUsosAlimentos = topAlimentosRaw.stream()
                .mapToLong(r -> ((Number) r[3]).longValue())
                .sum();

        List<AlimentoFrecuenteDTO> topAlimentos = new ArrayList<>();
        Map<String, Long> distribucionAlimentosPorCategoria = new LinkedHashMap<>();

        int rankLimit = Math.min(topAlimentosRaw.size(), 10);
        for (int i = 0; i < topAlimentosRaw.size(); i++) {
            Object[] row = topAlimentosRaw.get(i);
            Long alimentoId = ((Number) row[0]).longValue();
            String nombre = (String) row[1];
            String categoria = row[2] != null ? (String) row[2] : "OTRO";
            long vecesUtilizado = ((Number) row[3]).longValue();
            double pctUso = totalUsosAlimentos > 0
                    ? Math.round(((double) vecesUtilizado / totalUsosAlimentos) * 1000.0) / 10.0 : 0.0;

            if (i < rankLimit) {
                topAlimentos.add(AlimentoFrecuenteDTO.builder()
                        .alimentoId(alimentoId)
                        .nombre(nombre)
                        .categoria(categoria)
                        .vecesUtilizado(vecesUtilizado)
                        .porcentajeUso(pctUso)
                        .build());
            }

            distribucionAlimentosPorCategoria.put(
                    categoria,
                    distribucionAlimentosPorCategoria.getOrDefault(categoria, 0L) + vecesUtilizado
            );
        }

        Map<String, Long> plantillasPorTipoComida = new LinkedHashMap<>();
        for (Object[] row : plantillasPorTipoRaw) {
            String tipo = row[0] != null ? row[0].toString() : "OTRO";
            long count = ((Number) row[1]).longValue();
            plantillasPorTipoComida.put(tipo, count);
        }

        Map<String, Long> alimentosRegistradosPorCategoria = new LinkedHashMap<>();
        for (Object[] row : alimentosPorCatRaw) {
            String cat = row[0] != null ? (String) row[0] : "OTRO";
            long count = ((Number) row[1]).longValue();
            alimentosRegistradosPorCategoria.put(cat, count);
        }

        return ReporteDesempenoNutriologoDTO.builder()
                .nutriologoId(targetNutriologoId)
                .nombreNutriologo(nombreNutriologo)
                .correoNutriologo(correoNutriologo)
                .periodoInicio(fechaInicio)
                .periodoFin(fechaFin)
                .totalPacientesActivos(totalPacientesActivos)
                .totalCitasPeriodo(totalCitasPeriodo)
                .citasConcretadas(citasConcretadas)
                .citasCanceladas(citasCanceladas)
                .citasPendientes(citasPendientes)
                .citasNoAsistidas(citasNoAsistidas)
                .porcentajeAtencion(porcentajeAtencion)
                .porcentajeCancelacion(porcentajeCancelacion)
                .promedioCitasPorPaciente(promedioCitasPorPaciente)
                .nivelCargaTrabajo(nivelCargaTrabajo)
                .diagnosticoCarga(diagnosticoCarga)
                .topAlimentosAsignados(topAlimentos)
                .distribucionAlimentosPorCategoria(distribucionAlimentosPorCategoria)
                .totalAlimentosRegistrados(totalAlimentosRegistrados)
                .totalPlantillasRegistradas(totalPlantillasRegistradas)
                .plantillasPorTipoComida(plantillasPorTipoComida)
                .alimentosRegistradosPorCategoria(alimentosRegistradosPorCategoria)
                .build();
    }

    // ─── Reporte de Propuesta de Plan para Pacientes (Receptividad y Comunicación Proactiva) ───

    @Transactional(readOnly = true)
    public ReportePropuestaPlanDTO generarReportePropuestaPlan(Long nutriologoId, LocalDate inicio, LocalDate fin) {
        validarRangoFechas(inicio, fin);
        LocalDate fechaFin    = (fin    != null) ? fin    : LocalDate.now();
        LocalDate fechaInicio = (inicio != null) ? inicio : fechaFin.minusDays(29);
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("Rango de fechas inválido: La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        LocalDateTime dtInicio = fechaInicio.atStartOfDay();
        LocalDateTime dtFin    = fechaFin.atTime(23, 59, 59);

        boolean esAdmin = userContextService.isAdmin();
        Long targetNutriologoId = null;
        String nombreNutriologo = null;
        String correoNutriologo = null;

        long totalPacientes;
        List<SugerenciaPlantilla> sugerencias;

        if (nutriologoId != null) {
            if (!esAdmin) {
                Long currentId = userContextService.getCurrentUserId();
                if (!currentId.equals(nutriologoId)) {
                    throw new AccessDeniedException("Solo puedes consultar tu propio reporte de propuestas de plan.");
                }
            }
            targetNutriologoId = nutriologoId;
        } else if (!esAdmin) {
            targetNutriologoId = userContextService.getCurrentUserId();
        }

        final Long nutriologoIdFiltro = targetNutriologoId;

        if (nutriologoIdFiltro != null) {
            Nutriologo n = nutriologoRepository.findById(nutriologoIdFiltro).orElse(null);
            if (n != null) {
                nombreNutriologo = n.getNombre() + " " + n.getApellidoPaterno() + (n.getApellidoMaterno() != null ? " " + n.getApellidoMaterno() : "");
                correoNutriologo = n.getCorreo();
            }
            totalPacientes = pacienteRepository.countPacientesActivosByNutriologoId(nutriologoIdFiltro);
            sugerencias = sugerenciaRepository.findSugerenciasParaReportePorNutriologo(nutriologoIdFiltro, dtInicio, dtFin);
        } else {
            totalPacientes = pacienteRepository.countPacientesActivosTotal();
            sugerencias = sugerenciaRepository.findSugerenciasParaReporteGlobal(dtInicio, dtFin);
        }

        // 1. Tasa de Solicitud de Cambio y Métricas Generales
        long totalSolicitudes = sugerencias.size();
        Set<Long> pacientesConSolicitudSet = sugerencias.stream()
                .map(s -> s.getPaciente().getId())
                .collect(Collectors.toSet());
        long pacientesConSolicitud = pacientesConSolicitudSet.size();

        double tasaSolicitudCambio = totalPacientes > 0
                ? Math.round(((double) pacientesConSolicitud / totalPacientes) * 1000.0) / 10.0
                : 0.0;

        double promedioSolicitudesPorPaciente = totalPacientes > 0
                ? Math.round(((double) totalSolicitudes / totalPacientes) * 100.0) / 100.0
                : 0.0;

        long solicitudesAprobadas = sugerencias.stream().filter(s -> s.getEstado() == EstadoSugerencia.APROBADA).count();
        long solicitudesRechazadas = sugerencias.stream().filter(s -> s.getEstado() == EstadoSugerencia.RECHAZADA).count();
        long solicitudesPendientes = sugerencias.stream().filter(s -> s.getEstado() == EstadoSugerencia.PENDIENTE).count();
        long totalResueltas = solicitudesAprobadas + solicitudesRechazadas;

        double tasaAprobacion = totalResueltas > 0
                ? Math.round(((double) solicitudesAprobadas / totalResueltas) * 1000.0) / 10.0
                : 0.0;

        // 2. Gráfica y Desglose de Razones Principales ("Por Qué")
        Map<String, Long> razonesPrincipales = new LinkedHashMap<>();
        for (CategoriaMotivoSugerencia cat : CategoriaMotivoSugerencia.values()) {
            razonesPrincipales.put(cat.name(), 0L);
        }

        for (SugerenciaPlantilla s : sugerencias) {
            CategoriaMotivoSugerencia cat = s.getCategoriaMotivo() != null
                    ? s.getCategoriaMotivo()
                    : CategoriaMotivoSugerencia.PREFERENCIA_SABOR;
            razonesPrincipales.put(cat.name(), razonesPrincipales.getOrDefault(cat.name(), 0L) + 1);
        }

        String razonMasFrecuente = razonesPrincipales.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("PREFERENCIA_SABOR");

        // 3. Alimentos Problemáticos y Plantillas Sustituidas
        Map<Long, Long> conteoAlimentos = new HashMap<>();
        Map<Long, String> nombresAlimentos = new HashMap<>();
        Map<Long, String> categoriasAlimentos = new HashMap<>();

        Map<String, Long> plantillasMasModificadas = new LinkedHashMap<>();

        for (SugerenciaPlantilla s : sugerencias) {
            if (s.getPlantillaOriginalId() != null) {
                Plantilla orig = plantillaRepository.findById(s.getPlantillaOriginalId()).orElse(null);
                String plantillaKey = (orig != null) ? orig.getNombre() : ("Plantilla #" + s.getPlantillaOriginalId());
                plantillasMasModificadas.put(plantillaKey, plantillasMasModificadas.getOrDefault(plantillaKey, 0L) + 1);
            } else {
                String tipoKey = s.getTipoComida() != null ? s.getTipoComida().name() : "NUEVA_PROPUESTA";
                plantillasMasModificadas.put(tipoKey, plantillasMasModificadas.getOrDefault(tipoKey, 0L) + 1);
            }

            if (s.getDetalles() != null) {
                for (SugerenciaPlantillaDetalle d : s.getDetalles()) {
                    if (d.getAlimento() != null) {
                        Long aId = d.getAlimento().getId();
                        conteoAlimentos.put(aId, conteoAlimentos.getOrDefault(aId, 0L) + 1);
                        nombresAlimentos.put(aId, d.getAlimento().getNombre());
                        categoriasAlimentos.put(aId, d.getAlimento().getCategoria());
                    }
                }
            }
        }

        long totalOcurrenciasAlimentos = conteoAlimentos.values().stream().mapToLong(Long::longValue).sum();

        List<AlimentoSustituidoDTO> alimentosProblematicos = conteoAlimentos.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    Long aId = entry.getKey();
                    long count = entry.getValue();
                    double pct = totalOcurrenciasAlimentos > 0
                            ? Math.round(((double) count / totalOcurrenciasAlimentos) * 1000.0) / 10.0
                            : 0.0;
                    return AlimentoSustituidoDTO.builder()
                            .alimentoId(aId)
                            .nombre(nombresAlimentos.getOrDefault(aId, "Alimento #" + aId))
                            .categoria(categoriasAlimentos.getOrDefault(aId, "OTRO"))
                            .vecesSolicitado(count)
                            .porcentajeTotal(pct)
                            .build();
                })
                .collect(Collectors.toList());

        plantillasMasModificadas = plantillasMasModificadas.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(8)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // 4. Perfil de Pacientes (Nuevos <= 28 días vs Seguimiento > 28 días)
        long solicitudesPacientesNuevos = 0;
        long solicitudesPacientesSeguimiento = 0;

        for (SugerenciaPlantilla s : sugerencias) {
            Paciente p = s.getPaciente();
            LocalDateTime fechaReg = (p.getFechaRegistro() != null)
                    ? p.getFechaRegistro()
                    : (p.getFechaNacimiento() != null ? p.getFechaNacimiento().atStartOfDay() : s.getFechaEnvio());

            long diasDiferencia = Math.abs(Duration.between(fechaReg, s.getFechaEnvio()).toDays());

            if (diasDiferencia <= 28) {
                solicitudesPacientesNuevos++;
            } else {
                solicitudesPacientesSeguimiento++;
            }
        }

        double pctNuevos = totalSolicitudes > 0
                ? Math.round(((double) solicitudesPacientesNuevos / totalSolicitudes) * 1000.0) / 10.0
                : 0.0;
        double pctSeguimiento = totalSolicitudes > 0
                ? Math.round(((double) solicitudesPacientesSeguimiento / totalSolicitudes) * 1000.0) / 10.0
                : 0.0;

        long totalPacientesNuevos = 0;
        long totalPacientesSeguimiento = 0;
        List<Paciente> listaPacientesTarget;
        if (nutriologoIdFiltro != null) {
            listaPacientesTarget = pacienteRepository.findAll().stream()
                    .filter(p -> p.getNutriologo() != null && nutriologoIdFiltro.equals(p.getNutriologo().getId()))
                    .toList();
        } else {
            listaPacientesTarget = pacienteRepository.findAll();
        }

        LocalDateTime hace28Dias = LocalDateTime.now().minusDays(28);
        for (Paciente p : listaPacientesTarget) {
            LocalDateTime fechaReg = p.getFechaRegistro();
            if (fechaReg != null && fechaReg.isAfter(hace28Dias)) {
                totalPacientesNuevos++;
            } else {
                totalPacientesSeguimiento++;
            }
        }

        String diagnosticoReceptividad;
        if (totalSolicitudes == 0) {
            diagnosticoReceptividad = "Sin Solicitudes en el Periodo: No se han registrado peticiones de ajuste. Se sugiere validar activamente la receptividad en la próxima consulta.";
        } else if (tasaSolicitudCambio <= 15.0) {
            diagnosticoReceptividad = "Receptividad Dietética Alta: Solo el " + tasaSolicitudCambio + "% de los pacientes solicita ajustes. Los menús prescritos se adaptan favorablemente al estilo de vida de los pacientes.";
        } else if (tasaSolicitudCambio <= 35.0) {
            diagnosticoReceptividad = "Comunicación Proactiva Óptima: El " + tasaSolicitudCambio + "% de los pacientes propone modificaciones personalizadas, facilitando un ajuste fino de adherencia sin riesgo de deserción.";
        } else {
            diagnosticoReceptividad = "Alta Demanda de Adaptación: El " + tasaSolicitudCambio + "% de los pacientes requiere cambios. Se recomienda explorar alternativas de ingredientes y simplificar tiempos de preparación.";
        }

        String diagnosticoPerfil;
        if (totalSolicitudes == 0) {
            diagnosticoPerfil = "Proceso de Inducción Estable: No se registran fricciones tempranas en los planes asignados.";
        } else if (pctNuevos >= 60.0) {
            diagnosticoPerfil = "Foco en Proceso de Inducción (" + pctNuevos + "% nuevos): La mayoría de las solicitudes ocurren en las primeras 4 semanas. Se sugiere profundizar en la anamnesis de gustos y aversiones durante la primera consulta.";
        } else if (pctSeguimiento >= 60.0) {
            diagnosticoPerfil = "Foco en Prevención de Monotonía (" + pctSeguimiento + "% seguimiento): Los cambios provienen de pacientes consolidados. Se recomienda rotar plantillas y ofrecer mayor variedad gastronómica periódica.";
        } else {
            diagnosticoPerfil = "Distribución Homogénea: La demanda de cambios se reparte de forma equilibrada entre la fase de inducción (" + pctNuevos + "%) y el seguimiento regular (" + pctSeguimiento + "%).";
        }

        return ReportePropuestaPlanDTO.builder()
                .nutriologoId(nutriologoIdFiltro)
                .nombreNutriologo(nombreNutriologo)
                .correoNutriologo(correoNutriologo)
                .periodoInicio(fechaInicio)
                .periodoFin(fechaFin)
                .totalPacientes(totalPacientes)
                .pacientesConSolicitud(pacientesConSolicitud)
                .tasaSolicitudCambio(tasaSolicitudCambio)
                .totalSolicitudes(totalSolicitudes)
                .promedioSolicitudesPorPaciente(promedioSolicitudesPorPaciente)
                .solicitudesAprobadas(solicitudesAprobadas)
                .solicitudesRechazadas(solicitudesRechazadas)
                .solicitudesPendientes(solicitudesPendientes)
                .tasaAprobacion(tasaAprobacion)
                .razonesPrincipales(razonesPrincipales)
                .razonMasFrecuente(razonMasFrecuente)
                .alimentosProblematicos(alimentosProblematicos)
                .plantillasMasModificadas(plantillasMasModificadas)
                .solicitudesPacientesNuevos(solicitudesPacientesNuevos)
                .solicitudesPacientesSeguimiento(solicitudesPacientesSeguimiento)
                .porcentajePacientesNuevos(pctNuevos)
                .porcentajePacientesSeguimiento(pctSeguimiento)
                .totalPacientesNuevos(totalPacientesNuevos)
                .totalPacientesSeguimiento(totalPacientesSeguimiento)
                .diagnosticoPerfil(diagnosticoPerfil)
                .diagnosticoReceptividad(diagnosticoReceptividad)
                .build();
    }

    private void validarRangoFechas(LocalDate inicio, LocalDate fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new BadRequestException("Rango de fechas inválido: La fecha de inicio (" + inicio + ") no puede ser posterior a la fecha de fin (" + fin + ").");
        }
    }
}
