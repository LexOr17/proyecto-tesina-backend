package com.upemor.app_nutriologo.diario_comidas.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaRequestDTO;
import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaResponseDTO;
import com.upemor.app_nutriologo.diario_comidas.dtos.ObservacionRequestDTO;
import com.upemor.app_nutriologo.diario_comidas.service.DiarioComidaService;
import com.upemor.app_nutriologo.security.UserContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del diario de consumo de alimentos.
 * <p>
 * Ofrece endpoints para que los pacientes registren sus comidas y consulten su historial,
 * y para que los nutriólogos revisen el seguimiento de sus pacientes y agreguen observaciones clínicas.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@RestController
@RequestMapping("/diario-comida")
@RequiredArgsConstructor
public class DiarioComidaController {

    private final DiarioComidaService diarioService;
    private final UserContextService userContextService;

    // --- ENDPOINTS PACIENTE ---

    /**
     * Registra el consumo de una comida por parte del paciente autenticado.
     *
     * @param dto DTO con los detalles del consumo a registrar.
     * @return {@link ResponseEntity} con la respuesta estandarizada conteniendo la comida registrada y estado 201 (CREATED).
     */
    @PostMapping
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<DiarioComidaResponseDTO>> registrar(@Valid @RequestBody DiarioComidaRequestDTO dto) {
        return ResponseEntity.status(201).body(ApiResponse.created(diarioService.registrarComida(dto), "Comida registrada correctamente."));
    }

    /**
     * Actualiza un registro existente de comida del paciente dentro de la ventana permitida.
     *
     * @param id  Identificador único del registro a actualizar.
     * @param dto DTO con los datos actualizados.
     * @return {@link ResponseEntity} con la respuesta estandarizada y estado 200 (OK).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<DiarioComidaResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DiarioComidaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(diarioService.actualizarComida(id, dto), "Registro actualizado."));
    }

    /**
     * Obtiene el historial de registros de comidas del paciente actualmente autenticado.
     *
     * @return {@link ResponseEntity} con la lista de registros de diario y estado 200 (OK).
     */
    @GetMapping("/mi-historial")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<List<DiarioComidaResponseDTO>>> obtenerMiHistorial() {
        Long id = userContextService.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(diarioService.obtenerHistorialPorPaciente(id), "Tu historial ha sido recuperado."));
    }

    // --- ENDPOINTS NUTRIÓLOGO ---

    /**
     * Permite al nutriólogo o administrador consultar el historial completo de comidas de un paciente.
     *
     * @param pacienteId Identificador único del paciente a consultar.
     * @return {@link ResponseEntity} con la lista de registros del paciente y estado 200 (OK).
     */
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO','ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<DiarioComidaResponseDTO>>> verHistorialPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(ApiResponse.success(diarioService.obtenerHistorialPorPaciente(pacienteId), "Historial del paciente recuperado."));
    }

    /**
     * Permite al nutriólogo agregar una retroalimentación clínica a una comida registrada por un paciente.
     *
     * @param id  Identificador único del registro de diario.
     * @param dto DTO con el texto de la observación.
     * @return {@link ResponseEntity} con el registro actualizado y estado 200 (OK).
     */
    @PatchMapping("/{id}/observacion")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<DiarioComidaResponseDTO>> agregarObservacion(
            @PathVariable Long id,
            @Valid @RequestBody ObservacionRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(diarioService.agregarObservacion(id, dto), "Observación guardada exitosamente."));
    }

    // --- ENDPOINTS ADMIN Y NUTRIÓLOGO ---

    /**
     * Elimina permanentemente un registro de diario de comida.
     *
     * @param id Identificador único del registro a eliminar.
     * @return {@link ResponseEntity} con respuesta sin contenido y estado 200 (OK).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        diarioService.eliminarRegistro(id);
        return ResponseEntity.ok(ApiResponse.noContent("Registro eliminado permanentemente."));
    }
}