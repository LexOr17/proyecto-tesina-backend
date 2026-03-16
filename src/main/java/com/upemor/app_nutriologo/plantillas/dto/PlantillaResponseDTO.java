package com.upemor.app_nutriologo.plantillas.dto;

import com.upemor.app_nutriologo.alimento.dto.AlimentoResponseDTO;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;

import java.time.LocalDate;
import java.util.List;

public record PlantillaResponseDTO(
    Long id,
    String nombre,
    TipoComida tipoComida,
    LocalDate fechaCreacion,
    List<AlimentoResponseDTO> alimentos
) {}
