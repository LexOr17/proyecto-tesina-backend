package com.upemor.app_nutriologo.diario_extendido.dto;

import com.upemor.app_nutriologo.diario_extendido.enums.CalidadSueno;
import com.upemor.app_nutriologo.diario_extendido.enums.EstadoAnimo;

import java.time.LocalDate;
import java.util.List;

public record DiarioExtendidoResponseDTO(
        Integer id,
        Long pacienteId,
        LocalDate fecha,
        EstadoAnimo estadoAnimo,
        CalidadSueno calidadSueno,
        String sintomas,
        List<BebidaConsumidaDTO> bebidas,
        String actividadFisicaSugerida,
        String observacionesNutriologo
) {}