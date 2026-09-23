package com.upemor.app_nutriologo.sugerencia_plantilla.enums;

public enum CategoriaMotivoSugerencia {
    PREFERENCIA_SABOR("Preferencia de Sabor / Gusto Personal"),
    DISPONIBILIDAD_COSTO("Disponibilidad de Ingredientes / Presupuesto"),
    INTOLERANCIA_DIGESTION("Digestión, Intolerancia o Alergia"),
    TIEMPO_PREPARACION("Tiempo o Complejidad de Preparación"),
    SACIEDAD_ENERGIA("Nivel de Saciedad o Energía"),
    VARIEDAD_MENU("Variedad o Monotonía del Menú"),
    OTRO("Otro Motivo");

    private final String descripcion;

    CategoriaMotivoSugerencia(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
