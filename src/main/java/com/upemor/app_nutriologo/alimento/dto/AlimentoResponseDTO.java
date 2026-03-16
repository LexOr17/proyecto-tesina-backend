package com.upemor.app_nutriologo.alimento.dto;

import java.math.BigDecimal;

public record AlimentoResponseDTO(
    Long id,
    String nombre,
    String categoria,
    BigDecimal calorias,
    BigDecimal proteina,
    BigDecimal carbohidratos,
    BigDecimal grasas,
    BigDecimal porcionBase,
    String unidadPorcion,
    String imagenUrl,
    String registradoPor
) {}
