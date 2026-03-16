package com.upemor.app_nutriologo.plantillas.dto;

import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PlantillaRequestDTO(
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    String nombre,

    @NotNull(message = "El tipo de comida no puede ser nulo")
    TipoComida tipoComida,

    @NotNull(message = "El ID del usuario no puede ser nulo")
    Long usuarioId,

    @NotEmpty(message = "La lista de alimentos no puede estar vacía")
    List<Long> alimentoIds
) {}
