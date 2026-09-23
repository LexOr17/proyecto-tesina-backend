package com.upemor.app_nutriologo.calendario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Representa un bloque de hora disponible en la agenda del nutriólogo.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HoraLibreDTO {

    /** Hora formateada: "09:00" */
    private String hora;

    /** Fecha y hora completa en ISO-8601: "2026-08-09T09:00:00" */
    private String fechaHoraCompleta;
}
