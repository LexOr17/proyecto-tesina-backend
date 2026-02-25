package com.upemor.app_nutriologo.alimento.dto;

import jakarta.validation.constraints.DecimalMin;
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
    private BigDecimal calorias;

    @NotNull(message = "La proteína es obligatoria")
    @DecimalMin(value = "0.0", message = "La proteína no puede ser negativa")
    private BigDecimal proteina;

    @NotNull(message = "Los carbohidratos son obligatorios")
    @DecimalMin(value = "0.0", message = "Los carbohidratos no pueden ser negativos")
    private BigDecimal carbohidratos;

    @NotNull(message = "Las grasas son obligatorias")
    @DecimalMin(value = "0.0", message = "Las grasas no pueden ser negativas")
    private BigDecimal grasas;

    @NotNull(message = "La porción base es obligatoria")
    @DecimalMin(value = "0.1", message = "La porción base debe ser mayor a 0")
    private BigDecimal porcionBase;

    @NotBlank(message = "La unidad de porción es obligatoria")
    private String unidadPorcion;

    private String imagenUrl;
}
