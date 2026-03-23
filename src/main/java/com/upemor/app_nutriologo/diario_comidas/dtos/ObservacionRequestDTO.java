package com.upemor.app_nutriologo.diario_comidas.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ObservacionRequestDTO(
        @NotBlank(message = "La observación no puede estar vacía")
        @Size(max = 500, message = "La observación no debe exceder los 500 caracteres")
        String observacion
) {}
