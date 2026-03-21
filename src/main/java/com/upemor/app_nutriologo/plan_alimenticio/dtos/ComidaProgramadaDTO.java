package com.upemor.app_nutriologo.plan_alimenticio.dtos;

import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

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

