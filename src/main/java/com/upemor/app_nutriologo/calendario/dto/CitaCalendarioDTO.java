package com.upemor.app_nutriologo.calendario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Representa una cita simplificada para mostrar en el drawer del calendario.
 * Funciona tanto para la vista del nutriólogo como para la del paciente.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CitaCalendarioDTO {

    private Long id;

    // Solo en vista nutriólogo
    private Long pacienteId;
    private String pacienteNombre;

    // Solo en vista paciente
    private String nutriologoNombre;

    private String fechaHoraInicio;   // ISO-8601: "2026-08-09T09:00:00"
    private String fechaHoraFin;      // ISO-8601: "2026-08-09T10:00:00"
    private String estado;
    private String motivoCita;
}
