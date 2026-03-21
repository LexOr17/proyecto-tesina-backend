package com.upemor.app_nutriologo.meta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MetaRequestDTO {

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "La fecha objetivo no puede ser nula")
    @FutureOrPresent(message = "La fecha objetivo debe ser en el presente o en el futuro")
    @JsonProperty("fecha_objetivo")
    private LocalDate fechaObjetivo;

    @NotNull(message = "El ID del paciente no puede ser nulo")
    @JsonProperty("paciente_id")
    private Long pacienteId;
}
