package com.upemor.app_nutriologo.sugerencia_plantilla.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaResponseDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaPlantillaRequestDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaPlantillaResponseDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaRevisionDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.enums.EstadoSugerencia;
import com.upemor.app_nutriologo.sugerencia_plantilla.service.SugerenciaPlantillaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sugerencias-plantilla")
@RequiredArgsConstructor
public class SugerenciaPlantillaController {

    private final SugerenciaPlantillaService sugerenciaService;

    // -------------------------------------------------------
    // PACIENTE
    // -------------------------------------------------------

    /**
     * Envía una sugerencia de plantilla al nutriólogo.
     * El paciente puede copiar una de sus plantillas existentes
     * (indicando plantillaOriginalId) o crear una completamente nueva.
     */
    @PostMapping
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<SugerenciaPlantillaResponseDTO>> enviarSugerencia(
            @Valid @RequestBody SugerenciaPlantillaRequestDTO requestDTO) {

        SugerenciaPlantillaResponseDTO response = sugerenciaService.enviarSugerencia(requestDTO);
        return new ResponseEntity<>(
                ApiResponse.created(response, "Sugerencia enviada exitosamente al nutriólogo"),
                HttpStatus.CREATED
        );
    }

    /**
     * Devuelve las plantillas del plan alimenticio activo del paciente autenticado.
     * Útil para que el paciente seleccione una como base para su sugerencia.
     */
    @GetMapping("/mis-plantillas-plan")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<List<PlantillaResponseDTO>>> getMisPlantillasDePlan() {
        List<PlantillaResponseDTO> plantillas = sugerenciaService.obtenerPlantillasDelPlanActual();
        return ResponseEntity.ok(
                ApiResponse.success(plantillas, "Plantillas del plan alimenticio activo")
        );
    }

    /**
     * El paciente consulta su historial de sugerencias enviadas.
     */
    @GetMapping("/mis-sugerencias")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<Page<SugerenciaPlantillaResponseDTO>>> getMisSugerencias(
            @RequestParam(name = "estado", required = false) EstadoSugerencia estado,
            @PageableDefault(size = 10, sort = "fechaEnvio") Pageable pageable) {

        Page<SugerenciaPlantillaResponseDTO> page = sugerenciaService.obtenerSugerenciasDePaciente(pageable, estado);
        return ResponseEntity.ok(ApiResponse.success(page, "Historial de sugerencias del paciente"));
    }

    /**
     * El paciente cancela su propia sugerencia, sólo si aún está PENDIENTE.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<Void>> cancelarSugerencia(@PathVariable Long id) {
        sugerenciaService.eliminarSugerencia(id);
        return ResponseEntity.ok(ApiResponse.noContent("Sugerencia cancelada exitosamente"));
    }

    /**
     * El paciente edita una sugerencia propia que aún está en estado PENDIENTE.
     * Reemplaza por completo nombre, tipo de comida, motivo y lista de alimentos.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<SugerenciaPlantillaResponseDTO>> actualizarSugerencia(
            @PathVariable Long id,
            @Valid @RequestBody SugerenciaPlantillaRequestDTO requestDTO) {

        SugerenciaPlantillaResponseDTO response = sugerenciaService.actualizarSugerencia(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Sugerencia actualizada exitosamente"));
    }

    // -------------------------------------------------------
    // NUTRIÓLOGO
    // -------------------------------------------------------

    /**
     * El nutriólogo consulta todas las sugerencias recibidas de sus pacientes.
     */
    @GetMapping("/nutriologo/todas")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<Page<SugerenciaPlantillaResponseDTO>>> getSugerenciasNutriologo(
            @PageableDefault(size = 10, sort = "fechaEnvio") Pageable pageable) {

        Page<SugerenciaPlantillaResponseDTO> page = sugerenciaService.obtenerSugerenciasParaNutriologo(pageable);
        return ResponseEntity.ok(ApiResponse.success(page, "Sugerencias recibidas por el nutriólogo"));
    }

    /**
     * El nutriólogo consulta sólo las sugerencias con estado PENDIENTE.
     */
    @GetMapping("/nutriologo/pendientes")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<Page<SugerenciaPlantillaResponseDTO>>> getPendientesNutriologo(
            @PageableDefault(size = 10, sort = "fechaEnvio") Pageable pageable) {

        Page<SugerenciaPlantillaResponseDTO> page = sugerenciaService.obtenerPendientesParaNutriologo(pageable);
        return ResponseEntity.ok(ApiResponse.success(page, "Sugerencias pendientes de revisión"));
    }

    /**
     * El nutriólogo aprueba o rechaza una sugerencia.
     */
    @PatchMapping("/{id}/revisar")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<SugerenciaPlantillaResponseDTO>> revisarSugerencia(
            @PathVariable Long id,
            @Valid @RequestBody SugerenciaRevisionDTO revisionDTO) {

        SugerenciaPlantillaResponseDTO response = sugerenciaService.revisarSugerencia(id, revisionDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Sugerencia revisada exitosamente"));
    }

    // -------------------------------------------------------
    // COMPARTIDO
    // -------------------------------------------------------

    /**
     * Detalle de una sugerencia concreta (accesible por el paciente dueño o el nutriólogo).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<SugerenciaPlantillaResponseDTO>> getSugerenciaById(@PathVariable Long id) {
        SugerenciaPlantillaResponseDTO response = sugerenciaService.obtenerSugerenciaPorId(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Sugerencia encontrada"));
    }
}

