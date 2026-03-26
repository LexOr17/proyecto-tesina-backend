package com.upemor.app_nutriologo.meta.dto;

import com.upemor.app_nutriologo.meta.enums.EstadoMeta;

import java.time.LocalDate;

public record MetaResponseDTO(
        Integer id,

        String descripcion,

        LocalDate fechaObjetivo,

        EstadoMeta estado,

        String observaciones,

        Long pacienteId,

        Long nutriologoId
) {}