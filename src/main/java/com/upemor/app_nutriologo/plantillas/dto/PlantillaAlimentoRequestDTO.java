package com.upemor.app_nutriologo.plantillas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para la asignación de un alimento y su cantidad dentro de una plantilla.
 *
 * @param alimentoId Identificador único del alimento en el catálogo.
 * @param cantidad   Cantidad del alimento a incluir en la plantilla.
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public record PlantillaAlimentoRequestDTO(

        @NotNull(message = "El ID del alimento es obligatorio")
        Long alimentoId,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.1", message = "La cantidad debe ser mayor a 0")
        BigDecimal cantidad
) {}