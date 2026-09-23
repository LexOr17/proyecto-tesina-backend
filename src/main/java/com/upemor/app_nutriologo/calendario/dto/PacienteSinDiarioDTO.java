package com.upemor.app_nutriologo.calendario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Paciente que no ha registrado su diario de comida en la fecha consultada.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PacienteSinDiarioDTO {

    private Long pacienteId;
    private String nombreCompleto;
}
