package com.upemor.app_nutriologo.plantillas.dto;

import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlantillaRequestDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,

        @NotNull(message = "El tipo de comida no puede ser nulo")
        TipoComida tipoComida,

        @NotEmpty(message = "La plantilla debe tener al menos un alimento")
        @Valid // IMPORTANTE: Valida las cantidades de la lista interna
        List<PlantillaAlimentoRequestDTO> alimentos
) {}