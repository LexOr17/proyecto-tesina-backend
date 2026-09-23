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
     * multiplicando la cantidad de porciones por el valor nutricional base del alimento.
     *
     * @param items       lista de elementos (p. ej. PlantillaAlimento o SugerenciaPlantillaDetalle)
     * @param getCantidad extrae la cantidad de porciones del item
     * @param getPorcion  (mantenido por compatibilidad) extrae la porción base del alimento
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
            BigDecimal cantidad = getCantidad.apply(item);
            BigDecimal valor = getValor.apply(item);
            if (cantidad != null && valor != null) {
                total = total.add(valor.multiply(cantidad));
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}

