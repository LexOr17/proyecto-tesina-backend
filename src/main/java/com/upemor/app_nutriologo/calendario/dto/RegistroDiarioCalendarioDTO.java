package com.upemor.app_nutriologo.calendario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Registro del diario de comida del paciente para un horario específico.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistroDiarioCalendarioDTO {

    private Long id;
    private String horario;             // Ej: "DESAYUNO"
    private String estado;              // Ej: "CUMPLIDO", "SALTADA"
    private Long comidaProgramadaId;    // null si fue registrado sin plan
}
