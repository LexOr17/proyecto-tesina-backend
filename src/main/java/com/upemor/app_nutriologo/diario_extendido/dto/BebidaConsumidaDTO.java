package com.upemor.app_nutriologo.diario_extendido.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BebidaConsumidaDTO(
        Integer id,

        @NotBlank(message = "El nombre de la bebida es obligatorio")
        String nombre,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Double cantidad
) {}