package com.upemor.app_nutriologo.plantillas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PlantillaAlimentoRequestDTO(

        @NotNull(message = "El ID del alimento es obligatorio")
        Long alimentoId,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.1", message = "La cantidad debe ser mayor a 0")
        BigDecimal cantidad
) {}