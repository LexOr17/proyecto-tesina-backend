package com.upemor.app_nutriologo.calendario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Respuesta del endpoint GET /calendario/paciente/dia.
 * Contiene toda la información necesaria para el drawer del día en la vista del paciente.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarioDiaPacienteResponse {

    private String fecha;                                       // YYYY-MM-DD

    /** Citas del paciente en este día */
    private List<CitaCalendarioDTO> citas;

    /** Comidas programadas según el plan alimenticio activo para esta fecha */
    private List<ComidaProgramadaCalendarioDTO> comidasProgramadas;

    /** Registros de diario de comida que el paciente ya guardó para esta fecha */
    private List<RegistroDiarioCalendarioDTO> registrosDiario;

    /** Estado del diario de bienestar (null si no hay plan activo / no aplica) */
    private BienestarCalendarioDTO bienestar;
}
