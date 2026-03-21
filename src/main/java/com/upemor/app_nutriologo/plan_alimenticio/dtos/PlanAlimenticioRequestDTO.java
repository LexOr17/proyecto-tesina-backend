package com.upemor.app_nutriologo.plan_alimenticio.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record PlanAlimenticioRequestDTO(
        @NotBlank(message = "El nombre del plan no puede estar vacío")
        String nombre,

        @NotNull(message = "La fecha de inicio es obligatoria")
        @JsonProperty("fecha_inicio")
        LocalDate fechaInicio,

        @NotNull(message = "La duración en días es obligatoria")
        @Min(value = 1, message = "La duración mínima es de 1 día")
        @JsonProperty("duracion_dias")
        Integer duracionDias,

        @NotNull(message = "El ID del paciente es obligatorio")
        @JsonProperty("paciente_id")
        Long pacienteId,

        // Lo mantenemos por ahora, aunque en el Service lo validaremos con el Token
        @JsonProperty("nutriologo_id")
        Long nutriologoId,

        @NotEmpty(message = "El plan debe contener al menos una comida programada")
        @Valid
        List<ComidaProgramadaDTO> comidas
) {}

