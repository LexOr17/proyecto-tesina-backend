package com.upemor.app_nutriologo.plan_alimenticio.dto;

import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO inmutable (record) para programar una comida dentro de la solicitud de un plan alimenticio.
 *
 * @param plantillaId identificador primario de la plantilla de comida a utilizar.
 * @param diaPlan número de día secuencial en el que se consumirá la comida (mínimo 1).
 * @param horario tiempo o tipo de comida (ej. DESAYUNO, COMIDA, CENA).
 * @param observaciones notas o indicaciones específicas para esta toma.
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public record ComidaProgramadaDTO(
        @NotNull(message = "El ID de la plantilla es obligatorio")
        Long plantillaId,

        @NotNull(message = "El día del plan es obligatorio")
        @Min(value = 1, message = "El día del plan debe ser al menos 1")
        Integer diaPlan,

        @NotNull(message = "El horario es obligatorio")
        TipoComida horario,

        String observaciones
) {}

