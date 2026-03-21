package com.upemor.app_nutriologo.diario_extendido.dto;

import com.upemor.app_nutriologo.diario_extendido.enums.CalidadSueno;
import com.upemor.app_nutriologo.diario_extendido.enums.EstadoAnimo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record DiarioExtendidoPacienteRequestDTO(
        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotNull(message = "El estado de ánimo es obligatorio")
        EstadoAnimo estadoAnimo,

        @NotNull(message = "La calidad de sueño es obligatoria")
        CalidadSueno calidadSueno,

        String sintomas,

        @Valid
        List<BebidaConsumidaDTO> bebidas
) {}