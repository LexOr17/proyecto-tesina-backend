package com.upemor.app_nutriologo.diario_extendido.dto;

import com.upemor.app_nutriologo.diario_extendido.enums.TipoActividad;

public record DiarioExtendidoNutriologoRequestDTO(

        TipoActividad tipoActividadSugerida,

        String actividadDetalle,

        String observacionesNutriologo
) {}