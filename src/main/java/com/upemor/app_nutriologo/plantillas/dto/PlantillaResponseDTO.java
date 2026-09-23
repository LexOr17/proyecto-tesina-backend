package com.upemor.app_nutriologo.plantillas.dto;

import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO de respuesta con la información completa de una plantilla de comida.
 * <p>
 * Incluye los metadatos de la plantilla, el desglose de alimentos asociados
 * y los totales de macronutrientes calculados en base a las cantidades asignadas.
 * </p>
 *
 * @param id                 Identificador único de la plantilla.
 * @param nombre             Nombre descriptivo de la plantilla.
 * @param tipoComida         Tipo o momento de comida correspondiente.
 * @param fechaCreacion      Fecha de registro de la plantilla.
 * @param detalles           Lista con el desglose de alimentos y porciones.
 * @param totalCalorias      Total acumulado de calorías (kcal).
 * @param totalProteinas     Total acumulado de proteínas (g).
 * @param totalCarbohidratos Total acumulado de carbohidratos (g).
 * @param totalGrasas        Total acumulado de grasas (g).
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Builder
public record PlantillaResponseDTO(
        Long id,
        String nombre,
        TipoComida tipoComida,
        LocalDate fechaCreacion,
        List<PlantillaAlimentoResponseDTO> detalles,
        // Totales calculados en el Backend
        BigDecimal totalCalorias,
        BigDecimal totalProteinas,
        BigDecimal totalCarbohidratos,
        BigDecimal totalGrasas
) {}
