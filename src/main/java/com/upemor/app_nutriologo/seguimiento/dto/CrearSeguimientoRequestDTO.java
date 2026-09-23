package com.upemor.app_nutriologo.seguimiento.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CrearSeguimientoRequestDTO(
    @NotNull(message = "El ID del paciente es obligatorio")
    Long pacienteId,

    @NotNull(message = "La fecha de inicio es obligatoria")
    LocalDate fechaInicio,

    @NotNull(message = "La fecha de fin es obligatoria")
    LocalDate fechaFin,

    String observaciones,
    String conclusiones,
    String recomendaciones
) {}
