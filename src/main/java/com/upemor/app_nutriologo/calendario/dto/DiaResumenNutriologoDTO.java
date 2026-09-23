package com.upemor.app_nutriologo.calendario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Resumen de un día específico para el nutriólogo (vista del grid mensual).
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiaResumenNutriologoDTO {

    private String fecha;           // YYYY-MM-DD
    private boolean tieneCitas;
    private int cantidadCitas;
    private boolean tieneHorasLibres;
    private int pacientesSinDiario;
}
