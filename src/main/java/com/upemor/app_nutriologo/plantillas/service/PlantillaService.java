package com.upemor.app_nutriologo.plantillas.service;

import com.upemor.app_nutriologo.plantillas.dto.PlantillaRequestDTO;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlantillaService {

    PlantillaResponseDTO crearPlantilla(PlantillaRequestDTO requestDTO);

    Page<PlantillaResponseDTO> obtenerPlantillas(Pageable pageable);

    Page<PlantillaResponseDTO> buscarPlantillas(String keyword, String categoria, Pageable pageable);

    PlantillaResponseDTO buscarPlantillaPorId(Long id);

    PlantillaResponseDTO actualizarPlantilla(Long id, PlantillaRequestDTO requestDTO);

    void eliminarPlantilla(Long id);
}
