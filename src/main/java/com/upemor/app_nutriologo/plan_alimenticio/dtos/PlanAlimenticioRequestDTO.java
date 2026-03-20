package com.upemor.app_nutriologo.plan_alimenticio.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanAlimenticioRequestDTO {

    @NotBlank(message = "El nombre del plan no puede estar vacío")
    private String nombre;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La duración en días es obligatoria")
    @Min(value = 1, message = "La duración mínima es de 1 día")
    private Integer duracionDias;

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El ID del nutriólogo es obligatorio")
    private Long nutriologoId;
    // Nota: Si el ID del nutriólogo lo sacas del token JWT en tu seguridad,
    // podrías quitar este campo del DTO y tomarlo del contexto de Spring Security.

    @NotEmpty(message = "El plan debe contener al menos una comida programada")
    @Valid // ¡Muy importante! Esto le dice a Spring que también valide las reglas dentro de cada ComidaProgramadaDTO
    private List<ComidaProgramadaDTO> comidas;
}
