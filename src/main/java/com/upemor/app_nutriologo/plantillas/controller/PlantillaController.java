package com.upemor.app_nutriologo.plantillas.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaRequestDTO;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaResponseDTO;
import com.upemor.app_nutriologo.plantillas.service.PlantillaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plantillas")
@RequiredArgsConstructor
public class PlantillaController {

    private final PlantillaService plantillaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')") // Solo nutriólogos crean plantillas
    public ResponseEntity<ApiResponse<PlantillaResponseDTO>> createPlantilla(
            @Valid @RequestBody PlantillaRequestDTO requestDTO) {

        PlantillaResponseDTO createdPlantilla = plantillaService.crearPlantilla(requestDTO);
        return new ResponseEntity<>(
                ApiResponse.created(createdPlantilla, "Plantilla creada exitosamente"),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE', 'ADMINISTRADOR')") // Ambos pueden ver el catálogo
    public ResponseEntity<ApiResponse<Page<PlantillaResponseDTO>>> getAllPlantillas(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {

        Page<PlantillaResponseDTO> plantillas = plantillaService.obtenerPlantillas(pageable);
        return ResponseEntity.ok(ApiResponse.success(plantillas, "Listado de plantillas paginado obtenido"));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Page<PlantillaResponseDTO>>> buscarPlantillas(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "categoria", required = false, defaultValue = "") String categoria,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {

        Page<PlantillaResponseDTO> plantillas = plantillaService.buscarPlantillas(keyword, categoria, pageable);
        return ResponseEntity.ok(ApiResponse.success(plantillas, "Plantillas encontradas"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<PlantillaResponseDTO>> getPlantillaById(@PathVariable Long id) {

        PlantillaResponseDTO plantilla = plantillaService.buscarPlantillaPorId(id);
        return ResponseEntity.ok(ApiResponse.success(plantilla, "Plantilla encontrada"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')") // Solo el autor/nutriólogo debería editar
    public ResponseEntity<ApiResponse<PlantillaResponseDTO>> updatePlantilla(
            @PathVariable Long id,
            @Valid @RequestBody PlantillaRequestDTO requestDTO) {

        PlantillaResponseDTO updatedPlantilla = plantillaService.actualizarPlantilla(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedPlantilla, "Plantilla actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> deletePlantilla(@PathVariable Long id) {

        plantillaService.eliminarPlantilla(id);
        // Usamos tu método noContent para estandarizar la respuesta de éxito
        return ResponseEntity.ok(ApiResponse.noContent("Plantilla eliminada exitosamente"));
    }
}