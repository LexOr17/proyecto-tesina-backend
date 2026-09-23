package com.upemor.app_nutriologo.meta.dto;

import com.upemor.app_nutriologo.meta.enums.EstadoMeta;
import jakarta.validation.constraints.NotNull;

public record MetaEstadoDTO(
        @NotNull(message = "El estado es obligatorio")
        EstadoMeta nuevoEstado,

        String observaciones // Puede ser la razón del paciente o el feedback del nutriólogo
) {}
