package com.upemor.app_nutriologo.plan_alimenticio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO inmutable (record) para la solicitud de creación y configuración de un nuevo plan alimenticio.
 * <p>
 * Contiene los metadatos del plan, periodo de vigencia, paciente destinatario y la lista
 * de comidas programadas a incluir.
 * </p>
 *
 * @param nombre título o nombre descriptivo del plan.
 * @param fechaInicio fecha prevista para iniciar el régimen alimenticio.
 * @param duracionDias duración en días del plan (mínimo 1 día).
 * @param pacienteId identificador primario del paciente al que se prescribe el plan.
 * @param nutriologoId identificador primario del nutriólogo (opcional si se infiere del token de sesión).
 * @param comidas lista con al menos una comida programada estructurada por día y horario.
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
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

