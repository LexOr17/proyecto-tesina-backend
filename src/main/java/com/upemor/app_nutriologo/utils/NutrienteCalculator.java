package com.upemor.app_nutriologo.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;

/**
 * Utilidad para el cálculo de macronutrientes ajustados por porción.
 * <p>
 * Centraliza la fórmula: (cantidad / porciónBase) × valorNutricional
 * para evitar duplicación en los mappers de plantillas y sugerencias.
 */
public final class NutrienteCalculator {

    private NutrienteCalculator() {}

    /**
     * Suma el valor de un nutriente específico para una lista de items,
     * ajustando por la cantidad consumida respecto a la porción base del alimento.
     *
     * @param items       lista de elementos (p. ej. PlantillaAlimento o SugerenciaPlantillaDetalle)
     * @param getCantidad extrae la cantidad usada del item
     * @param getPorcion  extrae la porción base del alimento del item
     * @param getValor    extrae el valor nutricional base del alimento del item
     * @param <T>         tipo del elemento de la lista
     * @return total nutricional redondeado a 2 decimales
     */
    public static <T> BigDecimal sumar(
            List<T> items,
            Function<T, BigDecimal> getCantidad,
            Function<T, BigDecimal> getPorcion,
            Function<T, BigDecimal> getValor) {

        if (items == null || items.isEmpty()) return BigDecimal.ZERO;

        BigDecimal total = BigDecimal.ZERO;
        for (T item : items) {
            BigDecimal factor = getCantidad.apply(item)
                    .divide(getPorcion.apply(item), 4, RoundingMode.HALF_UP);
            total = total.add(getValor.apply(item).multiply(factor));
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}

