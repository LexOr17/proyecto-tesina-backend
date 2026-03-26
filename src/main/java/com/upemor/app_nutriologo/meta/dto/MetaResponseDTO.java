package com.upemor.app_nutriologo.meta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.upemor.app_nutriologo.meta.enums.EstadoMeta;

import java.time.LocalDate;

public record MetaResponseDTO(
        Integer id,

        String descripcion,

        @JsonProperty("fecha_objetivo")
        LocalDate fechaObjetivo,

        EstadoMeta estado,

        String observaciones,

        @JsonProperty("paciente_id")
        Long pacienteId,

        @JsonProperty("nutriologo_id")
        Long nutriologoId
) {}