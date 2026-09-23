package com.upemor.app_nutriologo.seguimiento.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoPreviewDTO {
    private Long pacienteId;
    private String pacienteNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private BigDecimal pesoInicio;
    private BigDecimal pesoFin;
    private BigDecimal diferenciaPeso;

    private Integer totalComidasRegistradas;
    private Integer comidasCumplidas;
    private Integer comidasModificadas;
    private Integer comidasSaltadas;
    private Double pctCumplimientoAlimentos;

    private Integer diasActividadRealizada;
    private Integer totalDiasExtendidos;
    private Double pctActividad;

    private Long totalMetas;
    private Long metasCumplidas;

    private Long totalCitas;
    private Long citasCompletadas;
}
