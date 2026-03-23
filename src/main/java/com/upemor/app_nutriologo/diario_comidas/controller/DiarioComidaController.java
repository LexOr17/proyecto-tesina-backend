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

@RestController
@RequestMapping("/diario-comida")
@RequiredArgsConstructor
public class DiarioComidaController {

    private final DiarioComidaService diarioService;
    private final UserContextService userContextService;

    // --- ENDPOINTS PACIENTE ---

    @PostMapping
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<DiarioComidaResponseDTO>> registrar(@Valid @RequestBody DiarioComidaRequestDTO dto) {
        return ResponseEntity.status(201).body(ApiResponse.created(diarioService.registrarComida(dto), "Comida registrada correctamente."));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<DiarioComidaResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DiarioComidaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(diarioService.actualizarComida(id, dto), "Registro actualizado."));
    }

    @GetMapping("/mi-historial")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<List<DiarioComidaResponseDTO>>> obtenerMiHistorial() {
        Long id = userContextService.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(diarioService.obtenerHistorialPorPaciente(id), "Tu historial ha sido recuperado."));
    }

    // --- ENDPOINTS NUTRIÓLOGO ---

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO','ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<List<DiarioComidaResponseDTO>>> verHistorialPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(ApiResponse.success(diarioService.obtenerHistorialPorPaciente(pacienteId), "Historial del paciente recuperado."));
    }

    @PatchMapping("/{id}/observacion")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<DiarioComidaResponseDTO>> agregarObservacion(
            @PathVariable Long id,
            @Valid @RequestBody ObservacionRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(diarioService.agregarObservacion(id, dto), "Observación guardada exitosamente."));
    }

    // --- ENDPOINTS ADMIN ---

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        diarioService.eliminarRegistro(id);
        return ResponseEntity.ok(ApiResponse.noContent("Registro eliminado permanentemente."));
    }
}