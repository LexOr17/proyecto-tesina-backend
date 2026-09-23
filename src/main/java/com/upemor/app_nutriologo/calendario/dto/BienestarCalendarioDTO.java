package com.upemor.app_nutriologo.calendario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Estado del diario de bienestar del paciente en la fecha consultada.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BienestarCalendarioDTO {

    private Long id;            // null si no está registrado
    private boolean registrado;
}
