package com.upemor.app_nutriologo.diario_extendido.dto;

import com.upemor.app_nutriologo.diario_extendido.enums.CalidadSueno;
import com.upemor.app_nutriologo.diario_extendido.enums.EstadoAnimo;
import com.upemor.app_nutriologo.diario_extendido.enums.Sintoma;
import com.upemor.app_nutriologo.diario_extendido.enums.TipoActividad;

import java.time.LocalDate;
import java.util.List;

public record DiarioExtendidoResponseDTO(
        Long id,
        Long pacienteId,
        LocalDate fecha,
        EstadoAnimo estadoAnimo,
        CalidadSueno calidadSueno,
        Sintoma sintomaPrincipal,
        String sintomaDetalle,
        Boolean actividadRealizada,
        List<BebidaConsumidaDTO> bebidas,
        TipoActividad tipoActividadSugerida,
        String actividadDetalle,
        String observacionesNutriologo
) {}