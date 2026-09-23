package com.upemor.app_nutriologo.plantillas.dto;

import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO para la creación y actualización de una plantilla de comida.
 *
 * @param nombre     Nombre descriptivo o título de la plantilla.
 * @param tipoComida Momento o categoría de comida asignada a la plantilla (desayuno, almuerzo, etc.).
 * @param alimentos  Lista de alimentos y cantidades que conforman la plantilla.
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public record PlantillaRequestDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,

        @NotNull(message = "El tipo de comida no puede ser nulo")
        TipoComida tipoComida,

        @NotEmpty(message = "La plantilla debe tener al menos un alimento")
        @Valid // IMPORTANTE: Valida las cantidades de la lista interna
        List<PlantillaAlimentoRequestDTO> alimentos
) {}