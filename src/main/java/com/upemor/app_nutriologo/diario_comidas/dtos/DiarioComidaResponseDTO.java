package com.upemor.app_nutriologo.diario_comidas.dtos;

import com.upemor.app_nutriologo.diario_comidas.enums.EstadoRegistro;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DiarioComidaResponseDTO(
        Long id,
        Long comidaProgramadaId,
        String nombreComidaSugerida,
        LocalDate fecha,
        TipoComida horario,
        EstadoRegistro estado,
        String detallesComidaReal,
        String observacionNutriologo,
        LocalDateTime fechaRegistroSistema
) {}
