package com.upemor.app_nutriologo.citas.controller;

import com.upemor.app_nutriologo.citas.dto.CitaEstadoUpdateDTO;
import com.upemor.app_nutriologo.citas.dto.CitaRequestDTO;
import com.upemor.app_nutriologo.citas.dto.CitaResponseDTO;
import com.upemor.app_nutriologo.citas.dto.CitaSolicitudDTO;
import com.upemor.app_nutriologo.citas.service.CitaService;
import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.security.UserContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;
    private final UserContextService userContextService;

    @PostMapping
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<CitaResponseDTO>> crearCita(@Valid @RequestBody CitaRequestDTO citaRequestDTO) {
        CitaResponseDTO nuevaCita = citaService.crearCita(citaRequestDTO);
        ApiResponse<CitaResponseDTO> response = ApiResponse.created(nuevaCita, "Cita creada exitosamente");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<List<CitaResponseDTO>>> obtenerTodasLasCitas() {
        List<CitaResponseDTO> citas = citaService.obtenerTodasLasCitas();
        ApiResponse<List<CitaResponseDTO>> response = ApiResponse.success(citas, "Citas obtenidas exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<CitaResponseDTO>> obtenerCitaPorId(@PathVariable Long id) {
        CitaResponseDTO cita = citaService.obtenerCitaPorId(id);
        ApiResponse<CitaResponseDTO> response = ApiResponse.success(cita, "Cita encontrada exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<List<CitaResponseDTO>>> obtenerCitasPorPacienteId(@PathVariable Long pacienteId) {
        List<CitaResponseDTO> citas = citaService.obtenerCitasPorPacienteId(pacienteId);
        ApiResponse<List<CitaResponseDTO>> response = ApiResponse.success(citas, "Citas del paciente obtenidas exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nutriologo/{nutriologoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<List<CitaResponseDTO>>> obtenerCitasPorNutriologoId(@PathVariable Long nutriologoId) {
        List<CitaResponseDTO> citas = citaService.obtenerCitasPorNutriologoId(nutriologoId);
        ApiResponse<List<CitaResponseDTO>> response = ApiResponse.success(citas, "Citas del nutriólogo obtenidas exitosamente");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<CitaResponseDTO>> actualizarCita(@PathVariable Long id, @Valid @RequestBody CitaRequestDTO citaRequestDTO) {
        CitaResponseDTO citaActualizada = citaService.actualizarCita(id, citaRequestDTO);
        ApiResponse<CitaResponseDTO> response = ApiResponse.success(citaActualizada, "Cita actualizada exitosamente");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarCita(@PathVariable Long id) {
        citaService.eliminarCita(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{citaId}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<CitaResponseDTO>> actualizarEstadoCita(@PathVariable Long citaId, @Valid @RequestBody CitaEstadoUpdateDTO estadoUpdateDTO) {
        CitaResponseDTO citaActualizada = citaService.actualizarEstadoCita(citaId, estadoUpdateDTO);
        ApiResponse<CitaResponseDTO> response = ApiResponse.success(citaActualizada, "El estado de la cita ha sido actualizado a " + estadoUpdateDTO.getNuevoEstado());
        return ResponseEntity.ok(response);
    }

    /** El pacienteId se toma del token para evitar que un paciente solicite citas en nombre de otro. */
    @PostMapping("/solicitar")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<CitaResponseDTO>> solicitarCita(@Valid @RequestBody CitaSolicitudDTO solicitudDTO) {
        Long pacienteId = userContextService.getCurrentUserId();
        CitaResponseDTO nuevaCita = citaService.solicitarCita(pacienteId, solicitudDTO);
        return new ResponseEntity<>(ApiResponse.created(nuevaCita, "Solicitud enviada"), HttpStatus.CREATED);
    }
}
