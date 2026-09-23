package com.upemor.app_nutriologo.alimento.dto;

import java.math.BigDecimal;

/**
 * DTO inmutable de respuesta (record) que representa la información pública de un alimento.
 * <p>
 * Proyecta los atributos del alimento junto con el nombre del usuario que lo dio de alta en el sistema.
 * </p>
 *
 * @param id identificador único del alimento.
 * @param nombre nombre descriptivo del alimento.
 * @param categoria categoría o grupo alimenticio.
 * @param calorias aporte calórico (kcal) por porción base.
 * @param proteina cantidad de proteína en gramos (g).
 * @param carbohidratos cantidad de carbohidratos en gramos (g).
 * @param grasas cantidad de grasas en gramos (g).
 * @param porcionBase cantidad de referencia de la porción.
 * @param unidadPorcion unidad de medida asociada a la porción base.
 * @param imagenUrl URL o ruta de acceso a la imagen representativa.
 * @param registradoPor nombre del usuario que registró el alimento en el catálogo.
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
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
