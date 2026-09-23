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

/**
 * Controlador REST para la gestión de plantillas de comidas nutricionales.
 * <p>
 * Provee endpoints para la creación, consulta paginada, búsqueda filtrada,
 * actualización y eliminación de plantillas utilizadas en el diseño de planes alimenticios.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@RestController
@RequestMapping("/plantillas")
@RequiredArgsConstructor
public class PlantillaController {

    private final PlantillaService plantillaService;

    /**
     * Registra una nueva plantilla de comida en el catálogo.
     *
     * @param requestDTO DTO con la información de la plantilla y los alimentos asociados.
     * @return {@link ResponseEntity} con la respuesta estandarizada conteniendo la plantilla creada y estado 201 (CREATED).
     */
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

    /**
     * Recupera el catálogo completo de plantillas de comida con soporte de paginación.
     *
     * @param pageable Configuración de paginación y ordenamiento (por defecto 10 elementos ordenados por nombre).
     * @return {@link ResponseEntity} con la página de plantillas y estado 200 (OK).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE', 'ADMINISTRADOR')") // Ambos pueden ver el catálogo
    public ResponseEntity<ApiResponse<Page<PlantillaResponseDTO>>> getAllPlantillas(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {

        Page<PlantillaResponseDTO> plantillas = plantillaService.obtenerPlantillas(pageable);
        return ResponseEntity.ok(ApiResponse.success(plantillas, "Listado de plantillas paginado obtenido"));
    }

    /**
     * Realiza una búsqueda paginada y filtrada de plantillas por término clave y categoría.
     *
     * @param keyword   Palabra clave para filtrar por nombre de plantilla o alimento.
     * @param categoria Categoría o tipo de comida a filtrar.
     * @param pageable  Configuración de paginación y ordenamiento.
     * @return {@link ResponseEntity} con la página de plantillas encontradas y estado 200 (OK).
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Page<PlantillaResponseDTO>>> buscarPlantillas(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "categoria", required = false, defaultValue = "") String categoria,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {

        Page<PlantillaResponseDTO> plantillas = plantillaService.buscarPlantillas(keyword, categoria, pageable);
        return ResponseEntity.ok(ApiResponse.success(plantillas, "Plantillas encontradas"));
    }

    /**
     * Obtiene el detalle completo y cálculos nutricionales de una plantilla por su identificador.
     *
     * @param id Identificador único de la plantilla.
     * @return {@link ResponseEntity} con la información de la plantilla y estado 200 (OK).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<PlantillaResponseDTO>> getPlantillaById(@PathVariable Long id) {

        PlantillaResponseDTO plantilla = plantillaService.buscarPlantillaPorId(id);
        return ResponseEntity.ok(ApiResponse.success(plantilla, "Plantilla encontrada"));
    }

    /**
     * Actualiza la información, tipo de comida y alimentos de una plantilla existente.
     *
     * @param id         Identificador único de la plantilla a modificar.
     * @param requestDTO DTO con los datos actualizados de la plantilla.
     * @return {@link ResponseEntity} con la información de la plantilla actualizada y estado 200 (OK).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')") // Solo el autor/nutriólogo debería editar
    public ResponseEntity<ApiResponse<PlantillaResponseDTO>> updatePlantilla(
            @PathVariable Long id,
            @Valid @RequestBody PlantillaRequestDTO requestDTO) {

        PlantillaResponseDTO updatedPlantilla = plantillaService.actualizarPlantilla(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedPlantilla, "Plantilla actualizada exitosamente"));
    }

    /**
     * Elimina una plantilla del sistema a partir de su identificador.
     *
     * @param id Identificador único de la plantilla a eliminar.
     * @return {@link ResponseEntity} con respuesta sin contenido y estado 200 (OK).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> deletePlantilla(@PathVariable Long id) {

        plantillaService.eliminarPlantilla(id);
        // Usamos tu método noContent para estandarizar la respuesta de éxito
        return ResponseEntity.ok(ApiResponse.noContent("Plantilla eliminada exitosamente"));
    }
}