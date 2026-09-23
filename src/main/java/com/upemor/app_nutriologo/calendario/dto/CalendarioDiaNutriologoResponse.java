package com.upemor.app_nutriologo.calendario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Respuesta del endpoint GET /calendario/nutriologo/dia.
 * Contiene toda la información necesaria para el drawer del día en la vista del nutriólogo.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarioDiaNutriologoResponse {

    private String fecha;                           // YYYY-MM-DD

    /** Citas agendadas en este día */
    private List<CitaCalendarioDTO> citas;

    /** Bloques de 1 hora disponibles (no ocupados por ninguna cita activa) */
    private List<HoraLibreDTO> horasLibres;

    /** Pacientes que tienen plan activo pero no han registrado su diario hoy */
    private List<PacienteSinDiarioDTO> pacientesSinDiario;
}
