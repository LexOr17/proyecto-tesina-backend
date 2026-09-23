package com.upemor.app_nutriologo.seguimiento.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeguimientoConsultaDTO {
    private Long id;
    private Long pacienteId;
    private String pacienteNombre;
    private Long nutriologoId;
    private String nutriologoNombre;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDateTime fechaConsulta;
    private LocalDateTime actualizadoEn;

    // Snapshot KPIs
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

    // Texto del nutriólogo
    private String observaciones;
    private String conclusiones;
    private String recomendaciones;
}
