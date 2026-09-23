package com.upemor.app_nutriologo.plantillas.dto;

import java.math.BigDecimal;

/**
 * DTO de respuesta con el detalle de un alimento asociado a una plantilla.
 * <p>
 * Proporciona información descriptiva del alimento, la cantidad asignada
 * y sus unidades de referencia para la porción base.
 * </p>
 *
 * @param alimentoId     Identificador único del alimento en el catálogo.
 * @param nombreAlimento Nombre descriptivo del alimento.
 * @param cantidad       Cantidad asignada del alimento en la plantilla.
 * @param porcionBase    Porción base de referencia del alimento.
 * @param unidadPorcion  Unidad de medida de la porción (g, ml, pieza, etc.).
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public record PlantillaAlimentoResponseDTO(
        Long alimentoId,
        String nombreAlimento,
        BigDecimal cantidad,
        BigDecimal porcionBase,
        String unidadPorcion
) {}
