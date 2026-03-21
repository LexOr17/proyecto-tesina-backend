package com.upemor.app_nutriologo.plantillas.dto;

import java.math.BigDecimal;

public record PlantillaAlimentoResponseDTO(
        Long alimentoId,
        String nombreAlimento,
        BigDecimal cantidad,
        String unidadPorcion
) {}
