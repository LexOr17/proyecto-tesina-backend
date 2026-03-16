package com.upemor.app_nutriologo.plantillas.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaRequestDTO;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaResponseDTO;
import com.upemor.app_nutriologo.plantillas.service.PlantillaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plantillas")
public class PlantillaController {

    private final PlantillaService plantillaService;

    public PlantillaController(PlantillaService plantillaService) {
        this.plantillaService = plantillaService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PlantillaResponseDTO>> createPlantilla(@Valid @RequestBody PlantillaRequestDTO requestDTO) {
        PlantillaResponseDTO createdPlantilla = plantillaService.crearPlantilla(requestDTO);
        ApiResponse<PlantillaResponseDTO> response = ApiResponse.<PlantillaResponseDTO>builder()
                .estatus(HttpStatus.CREATED.value())
                .mensaje("Plantilla creada exitosamente")
                .datos(createdPlantilla)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PlantillaResponseDTO>>> getAllPlantillas(Pageable pageable) {
        Page<PlantillaResponseDTO> plantillas = plantillaService.obtenerPlantillas(pageable);
        ApiResponse<Page<PlantillaResponseDTO>> response = ApiResponse.<Page<PlantillaResponseDTO>>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Listado de plantillas paginado")
                .datos(plantillas)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlantillaResponseDTO>> getPlantillaById(@PathVariable Long id) {
        PlantillaResponseDTO plantilla = plantillaService.buscarPlantillaPorId(id);
        ApiResponse<PlantillaResponseDTO> response = ApiResponse.<PlantillaResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Plantilla encontrada")
                .datos(plantilla)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlantillaResponseDTO>> updatePlantilla(@PathVariable Long id, @Valid @RequestBody PlantillaRequestDTO requestDTO) {
        PlantillaResponseDTO updatedPlantilla = plantillaService.actualizarPlantilla(id, requestDTO);
        ApiResponse<PlantillaResponseDTO> response = ApiResponse.<PlantillaResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Plantilla actualizada exitosamente")
                .datos(updatedPlantilla)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlantilla(@PathVariable Long id) {
        plantillaService.eliminarPlantilla(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .estatus(HttpStatus.NO_CONTENT.value())
                .mensaje("Plantilla eliminada exitosamente")
                .build();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
