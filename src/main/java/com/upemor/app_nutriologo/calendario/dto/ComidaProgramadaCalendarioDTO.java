package com.upemor.app_nutriologo.calendario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * Comida programada del plan alimenticio para mostrar en el drawer del paciente.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComidaProgramadaCalendarioDTO {

    private Long id;
    private String horario;           // Ej: "DESAYUNO", "ALMUERZO"
    private String plantillaNombre;   // Nombre de la plantilla de comida
    private String observaciones;
}
