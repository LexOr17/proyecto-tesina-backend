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
public class ReporteAsistenciaDTO {

    // ─── Totales generales ─────────────────────────────────────────────────
    private long totalCitas;
    private long citasAsistidas;
    private long citasNoAsistidas;
    private long citasCanceladas;
    private long citasProgramadas;   // PROGRAMADA + SOLICITADA (aún vigentes)
    private long citasReprogramadas; // REPROGRAMACION_SOLICITADA

    // ─── Indicadores de eficiencia ─────────────────────────────────────────
    /** % de citas con estado ASISTIO sobre (ASISTIO + NO_ASISTIO) */
    private double porcentajeAsistencia;

    /** % de citas CANCELADA sobre el total de citas del periodo */
    private double porcentajeCancelacion;

    /** % de citas NO_ASISTIO sobre (ASISTIO + NO_ASISTIO) */
    private double porcentajeInasistencia;

    // ─── Demanda de servicios ──────────────────────────────────────────────
    /** Conteo de citas agrupado por MotivoCita */
    private Map<String, Long> demandaPorMotivo;

    /** Motivo de cita con mayor frecuencia */
    private String motivoMasFrecuente;

    // ─── Metadatos ─────────────────────────────────────────────────────────
    private LocalDate periodoInicio;
    private LocalDate periodoFin;

    /** Nombre del nutriólogo filtrado (null = todos) */
    private String nombreNutriologo;
}
