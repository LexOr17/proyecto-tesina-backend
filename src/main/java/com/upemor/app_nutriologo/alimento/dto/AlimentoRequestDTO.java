package com.upemor.app_nutriologo.alimento.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para la creación y actualización de alimentos en el catálogo del sistema.
 * <p>
 * Incluye validaciones de presencia, límites numéricos y formato decimal para garantizar
 * la consistencia de los datos nutrimentales ingresados.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlimentoRequestDTO {

    /**
     * Nombre descriptivo del alimento.
     */
    @NotBlank(message = "El nombre del alimento es obligatorio")
    private String nombre;

    /**
     * Categoría o grupo nutricional al que pertenece el alimento.
     */
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    /**
     * Aporte calórico (kcal) correspondiente a la porción base.
     */
    @NotNull(message = "Las calorías son obligatorias")
    @DecimalMin(value = "0.0", message = "Las calorías no pueden ser negativas")
    @Digits(integer = 4, fraction = 2, message = "El formato de calorías es inválido (máximo 4 enteros y 2 decimales)")
    private BigDecimal calorias;

    /**
     * Cantidad de proteína en gramos (g).
     */
    @NotNull(message = "La proteína es obligatoria")
    @DecimalMin(value = "0.0", message = "La proteína no puede ser negativa")
    @Digits(integer = 3, fraction = 2, message = "El formato de proteína es inválido (máximo 3 enteros y 2 decimales)")
    private BigDecimal proteina;

    /**
     * Cantidad de carbohidratos en gramos (g).
     */
    @NotNull(message = "Los carbohidratos son obligatorios")
    @DecimalMin(value = "0.0", message = "Los carbohidratos no pueden ser negativos")
    @Digits(integer = 3, fraction = 2, message = "El formato de carbohidratos es inválido (máximo 3 enteros y 2 decimales)")
    private BigDecimal carbohidratos;

    /**
     * Cantidad de grasas en gramos (g).
     */
    @NotNull(message = "Las grasas son obligatorias")
    @DecimalMin(value = "0.0", message = "Las grasas no pueden ser negativas")
    @Digits(integer = 3, fraction = 2, message = "El formato de grasas es inválido (máximo 3 enteros y 2 decimales)")
    private BigDecimal grasas;

    /**
     * Cantidad numérica representativa de la porción base (ej. 100).
     */
    @NotNull(message = "La porción base es obligatoria")
    @DecimalMin(value = "0.1", message = "La porción base debe ser mayor a 0")
    @Digits(integer = 4, fraction = 2, message = "El formato de porción base es inválido (máximo 4 enteros y 2 decimales)")
    private BigDecimal porcionBase;

    /**
     * Unidad de medida de la porción (ej. gramos, ml, pieza, taza).
     */
    @NotBlank(message = "La unidad de porción es obligatoria")
    private String unidadPorcion;

    /**
     * URL o ruta opcional a la imagen del alimento.
     */
    private String imagenUrl;
}
