package com.upemor.app_nutriologo.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteSeguimientoPlanDTO {
    // Cumplimiento de metas
    private long totalMetas;
    private long metasCumplidas;
    private long metasEnProgreso;   // PENDIENTE + POR_VALIDAR
    private long metasNoCumplidas;  // RECHAZADA + NO_CUMPLIDA
    private double porcentajeCumplimientoMetas;

    // Porcentaje de Apego al Plan Alimenticio
    private long totalRegistros;
    private long registrosCumplidos;
    private long registrosModificados;
    private long registrosSaltados;
    private long registrosNoRegistrados;
    private double porcentajeApego;

    // Gráfica de Días de Desviación
    private Map<String, Long> desviacionPorDia;

    // Metadatos
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private String nombrePaciente;
}
