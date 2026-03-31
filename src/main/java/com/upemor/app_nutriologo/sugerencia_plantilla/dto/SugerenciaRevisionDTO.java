package com.upemor.app_nutriologo.sugerencia_plantilla.dto;

import com.upemor.app_nutriologo.sugerencia_plantilla.enums.EstadoSugerencia;
import jakarta.validation.constraints.NotNull;

public record SugerenciaRevisionDTO(

        @NotNull(message = "El estado de revisión no puede ser nulo")
        EstadoSugerencia estado,

        /** Comentario opcional del nutriólogo al aprobar o rechazar */
        String comentarioNutriologo
) {}

