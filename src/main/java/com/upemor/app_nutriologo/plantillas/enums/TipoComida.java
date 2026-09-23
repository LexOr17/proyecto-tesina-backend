package com.upemor.app_nutriologo.plantillas.enums;

/**
 * Enumeración que define los diferentes momentos o tipos de comida en un día.
 * <p>
 * Se utiliza para categorizar y organizar las plantillas y comidas programadas
 * dentro de los planes alimenticios y diarios de comidas.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public enum TipoComida {
    /**
     * Primera comida del día tomada por la mañana.
     */
    DESAYUNO,

    /**
     * Comida principal consumida a mitad del día o mediodía.
     */
    ALMUERZO,

    /**
     * Refrigerio ligero consumido entre las comidas principales o por la tarde.
     */
    MERIENDA,

    /**
     * Última comida del día tomada por la noche.
     */
    CENA,
}
