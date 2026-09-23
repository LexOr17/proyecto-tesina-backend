package com.upemor.app_nutriologo.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDesempenoNutriologoDTO {

    // Metadatos
    private Long nutriologoId;
    private String nombreNutriologo;
    private String correoNutriologo;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;

    // 1. Gestión de Pacientes y Carga de Trabajo
    private long totalPacientesActivos;
    private long totalCitasPeriodo;
    private long citasConcretadas;        // ASISTIO
    private long citasCanceladas;         // CANCELADA / CANCELACION_SOLICITADA
    private long citasPendientes;         // PROGRAMADA / SOLICITADA
    private long citasNoAsistidas;        // NO_ASISTIO
    private double porcentajeAtencion;    // (citasConcretadas / totalCitasPeriodo) * 100
    private double porcentajeCancelacion; // (citasCanceladas / totalCitasPeriodo) * 100
    private double promedioCitasPorPaciente;
    private String nivelCargaTrabajo;     // BAJA, MODERADA, OPTIMA, ALTA, SOBRECARGA
    private String diagnosticoCarga;

    // 2. Contenido y Planeación (Prescripción en Plantillas)
    private List<AlimentoFrecuenteDTO> topAlimentosAsignados;
    private Map<String, Long> distribucionAlimentosPorCategoria;

    // 3. Aportación a la Base de Datos (Contribución al sistema)
    private long totalAlimentosRegistrados;
    private long totalPlantillasRegistradas;
    private Map<String, Long> plantillasPorTipoComida;
    private Map<String, Long> alimentosRegistradosPorCategoria;
}
