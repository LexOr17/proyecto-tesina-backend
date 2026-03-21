package com.upemor.app_nutriologo.plantillas.dto;

import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
