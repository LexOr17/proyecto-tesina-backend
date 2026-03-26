package com.upemor.app_nutriologo.meta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MetaRequestDTO(

        @NotBlank(message = "La descripción no puede estar vacía")
        String descripcion,

        @NotNull(message = "La fecha objetivo no puede ser nula")
        @FutureOrPresent(message = "La fecha objetivo debe ser en el presente o en el futuro")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate fechaObjetivo,

        @NotNull(message = "El ID del paciente no puede ser nulo")
        Long pacienteId
) {}