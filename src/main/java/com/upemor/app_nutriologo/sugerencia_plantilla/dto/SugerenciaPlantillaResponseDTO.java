package com.upemor.app_nutriologo.sugerencia_plantilla.dto;

import com.upemor.app_nutriologo.plantillas.dto.PlantillaAlimentoResponseDTO;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import com.upemor.app_nutriologo.sugerencia_plantilla.enums.CategoriaMotivoSugerencia;
import com.upemor.app_nutriologo.sugerencia_plantilla.enums.EstadoSugerencia;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SugerenciaPlantillaResponseDTO(
        Long id,
        String nombre,
        TipoComida tipoComida,
        EstadoSugerencia estado,
        CategoriaMotivoSugerencia categoriaMotivo,
        String motivoSugerencia,
        String comentarioNutriologo,
        Long plantillaOriginalId,
        LocalDateTime fechaEnvio,
        LocalDateTime fechaRevision,
        Long pacienteId,
        Long nutriologoId,
        List<PlantillaAlimentoResponseDTO> detalles,
        BigDecimal totalCalorias,
        BigDecimal totalProteinas,
        BigDecimal totalCarbohidratos,
        BigDecimal totalGrasas
) {}
