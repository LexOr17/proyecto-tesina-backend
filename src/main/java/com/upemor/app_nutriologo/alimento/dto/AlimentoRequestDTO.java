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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlimentoRequestDTO {

    @NotBlank(message = "El nombre del alimento es obligatorio")
    private String nombre;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotNull(message = "Las calorías son obligatorias")
    @DecimalMin(value = "0.0", message = "Las calorías no pueden ser negativas")
    @Digits(integer = 4, fraction = 2, message = "El formato de calorías es inválido (máximo 4 enteros y 2 decimales)")
    private BigDecimal calorias;

    @NotNull(message = "La proteína es obligatoria")
    @DecimalMin(value = "0.0", message = "La proteína no puede ser negativa")
    @Digits(integer = 3, fraction = 2, message = "El formato de proteína es inválido (máximo 3 enteros y 2 decimales)")
    private BigDecimal proteina;

    @NotNull(message = "Los carbohidratos son obligatorios")
    @DecimalMin(value = "0.0", message = "Los carbohidratos no pueden ser negativos")
    @Digits(integer = 3, fraction = 2, message = "El formato de carbohidratos es inválido (máximo 3 enteros y 2 decimales)")
    private BigDecimal carbohidratos;

    @NotNull(message = "Las grasas son obligatorias")
    @DecimalMin(value = "0.0", message = "Las grasas no pueden ser negativas")
    @Digits(integer = 3, fraction = 2, message = "El formato de grasas es inválido (máximo 3 enteros y 2 decimales)")
    private BigDecimal grasas;

    @NotNull(message = "La porción base es obligatoria")
    @DecimalMin(value = "0.1", message = "La porción base debe ser mayor a 0")
    @Digits(integer = 4, fraction = 2, message = "El formato de porción base es inválido (máximo 4 enteros y 2 decimales)")
    private BigDecimal porcionBase;

    @NotBlank(message = "La unidad de porción es obligatoria")
    private String unidadPorcion;

    private String imagenUrl;
}
