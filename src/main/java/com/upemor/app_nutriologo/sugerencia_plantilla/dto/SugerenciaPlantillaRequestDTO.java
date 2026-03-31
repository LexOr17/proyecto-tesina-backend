package com.upemor.app_nutriologo.sugerencia_plantilla.dto;

import com.upemor.app_nutriologo.plantillas.dto.PlantillaAlimentoRequestDTO;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SugerenciaPlantillaRequestDTO(

        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,

        @NotNull(message = "El tipo de comida no puede ser nulo")
        TipoComida tipoComida,

        /** Justificación o razón de la sugerencia (opcional) */
        String motivoSugerencia,

        /**
         * ID de la plantilla original que se usó como base.
         * Null si la sugerencia es creada desde cero.
         */
        Long plantillaOriginalId,

        @NotEmpty(message = "La sugerencia debe tener al menos un alimento")
        @Valid
        List<PlantillaAlimentoRequestDTO> alimentos
) {}

