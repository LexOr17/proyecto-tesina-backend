package com.upemor.app_nutriologo.plan_alimenticio.dtos;

import com.upemor.app_nutriologo.plantillas.enums.TipoComida;

public record ComidaProgramadaResponseDTO(
        Long id,
        Long plantillaId,
        String plantillaNombre,
        Integer diaPlan,
        TipoComida horario,
        String observaciones
) {}