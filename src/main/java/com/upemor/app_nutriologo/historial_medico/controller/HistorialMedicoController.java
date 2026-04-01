package com.upemor.app_nutriologo.historial_medico.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.historial_medico.dto.HistorialMedicoRequestDTO;
import com.upemor.app_nutriologo.historial_medico.dto.HistorialMedicoResponseDTO;
import com.upemor.app_nutriologo.historial_medico.service.HistorialMedicoService;
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
@RequiredArgsConstructor
@RequestMapping("/historial-medico")
public class HistorialMedicoController {

    private final HistorialMedicoService historialService;

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------

    /**
     * NUTRIÓLOGO: registra un nuevo historial médico para el paciente indicado.
     * Si no se envía medidaAntropometricaId, se vincula automáticamente
     * la medición más reciente registrada del paciente.
     */
    @PostMapping("/{pacienteId}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<HistorialMedicoResponseDTO>> registrarHistorial(
            @PathVariable Long pacienteId,
            @Valid @RequestBody HistorialMedicoRequestDTO requestDTO) {

        HistorialMedicoResponseDTO response = historialService.registrarHistorial(pacienteId, requestDTO);
        return new ResponseEntity<>(
                ApiResponse.created(response, "Historial médico registrado exitosamente"),
                HttpStatus.CREATED
        );
    }

    // -------------------------------------------------------
    // READ
    // -------------------------------------------------------

    /**
     * NUTRIÓLOGO / PACIENTE: lista los historiales de un paciente de forma paginada,
     * del más reciente al más antiguo.
     * Parámetros de paginación: page, size, sort (por defecto: page=0, size=10)
     */
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<Page<HistorialMedicoResponseDTO>>> obtenerHistoriales(
            @PathVariable Long pacienteId,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<HistorialMedicoResponseDTO> historiales =
                historialService.obtenerHistorialesPorPaciente(pacienteId, pageable);
        return ResponseEntity.ok(
                ApiResponse.success(historiales, "Historiales médicos obtenidos exitosamente")
        );
    }

    /**
     * NUTRIÓLOGO / PACIENTE: obtiene el historial médico más reciente del paciente.
     */
    @GetMapping("/paciente/{pacienteId}/ultimo")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<HistorialMedicoResponseDTO>> obtenerUltimoHistorial(
            @PathVariable Long pacienteId) {

        HistorialMedicoResponseDTO historial = historialService.obtenerUltimoHistorial(pacienteId);
        return ResponseEntity.ok(
                ApiResponse.success(historial, "Último historial médico obtenido exitosamente")
        );
    }

    /**
     * NUTRIÓLOGO / PACIENTE: obtiene un historial médico específico por su ID.
     */
    @GetMapping("/{historialId}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<HistorialMedicoResponseDTO>> obtenerHistorialPorId(
            @PathVariable Long historialId) {

        HistorialMedicoResponseDTO historial = historialService.obtenerHistorialPorId(historialId);
        return ResponseEntity.ok(
                ApiResponse.success(historial, "Historial médico obtenido exitosamente")
        );
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------

    /**
     * NUTRIÓLOGO: actualiza un historial médico existente.
     */
    @PutMapping("/{historialId}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<HistorialMedicoResponseDTO>> actualizarHistorial(
            @PathVariable Long historialId,
            @Valid @RequestBody HistorialMedicoRequestDTO requestDTO) {

        HistorialMedicoResponseDTO response = historialService.actualizarHistorial(historialId, requestDTO);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Historial médico actualizado exitosamente")
        );
    }
    /**
     * NUTRIÓLOGO: elimina un historial médico.
     */
    @DeleteMapping("/{historialId}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<Void>> eliminarHistorial(@PathVariable Long historialId) {
        historialService.eliminarHistorial(historialId);
        return ResponseEntity.ok(
                ApiResponse.noContent("Historial médico eliminado exitosamente")
        );
    }
}
