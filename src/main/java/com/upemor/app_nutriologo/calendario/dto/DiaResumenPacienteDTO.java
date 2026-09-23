package com.upemor.app_nutriologo.calendario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Resumen de un día específico para el paciente (vista del grid mensual).
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiaResumenPacienteDTO {

    private String fecha;               // YYYY-MM-DD
    private boolean tieneCita;
    private boolean tienePlan;
    private boolean diarioCompleto;     // true si todos los horarios del plan están registrados
    private boolean bienestarRegistrado;
}
