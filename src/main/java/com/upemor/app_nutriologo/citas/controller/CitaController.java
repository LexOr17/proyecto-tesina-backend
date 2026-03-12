package com.upemor.app_nutriologo.citas.controller;

import com.upemor.app_nutriologo.citas.dto.*;
import com.upemor.app_nutriologo.citas.service.CitaService;
import com.upemor.app_nutriologo.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @PostMapping
    public ResponseEntity<ApiResponse<CitaResponseDTO>> crearCita(@Valid @RequestBody CitaRequestDTO citaRequestDTO) {
        CitaResponseDTO nuevaCita = citaService.crearCita(citaRequestDTO);
        ApiResponse<CitaResponseDTO> response = ApiResponse.created(nuevaCita, "Cita creada exitosamente (por nutriólogo)");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CitaResponseDTO>>> obtenerTodasLasCitas() {
        List<CitaResponseDTO> citas = citaService.obtenerTodasLasCitas();
        ApiResponse<List<CitaResponseDTO>> response = ApiResponse.success(citas, "Citas obtenidas exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CitaResponseDTO>> obtenerCitaPorId(@PathVariable Long id) {
        CitaResponseDTO cita = citaService.obtenerCitaPorId(id);
        ApiResponse<CitaResponseDTO> response = ApiResponse.success(cita, "Cita encontrada exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<ApiResponse<List<CitaResponseDTO>>> obtenerCitasPorPacienteId(@PathVariable Long pacienteId) {
        List<CitaResponseDTO> citas = citaService.obtenerCitasPorPacienteId(pacienteId);
        ApiResponse<List<CitaResponseDTO>> response = ApiResponse.success(citas, "Citas del paciente obtenidas exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nutriologo/{nutriologoId}")
    public ResponseEntity<ApiResponse<List<CitaResponseDTO>>> obtenerCitasPorNutriologoId(@PathVariable Long nutriologoId) {
        List<CitaResponseDTO> citas = citaService.obtenerCitasPorNutriologoId(nutriologoId);
        ApiResponse<List<CitaResponseDTO>> response = ApiResponse.success(citas, "Citas del nutriólogo obtenidas exitosamente");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CitaResponseDTO>> actualizarCita(@PathVariable Long id, @Valid @RequestBody CitaRequestDTO citaRequestDTO) {
        CitaResponseDTO citaActualizada = citaService.actualizarCita(id, citaRequestDTO);
        ApiResponse<CitaResponseDTO> response = ApiResponse.success(citaActualizada, "Cita actualizada exitosamente");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCita(@PathVariable Long id) {
        citaService.eliminarCita(id);
        ApiResponse<Void> response = ApiResponse.success(null, "Cita eliminada permanentemente");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/solicitar/{pacienteId}")
    public ResponseEntity<ApiResponse<CitaResponseDTO>> solicitarCita(@PathVariable Long pacienteId, @Valid @RequestBody CitaSolicitudDTO solicitudDTO) {
        CitaResponseDTO citaSolicitada = citaService.solicitarCita(pacienteId, solicitudDTO);
        ApiResponse<CitaResponseDTO> response = ApiResponse.created(citaSolicitada, "Cita solicitada exitosamente. Esperando confirmación del nutriólogo.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{citaId}/cancelar")
    public ResponseEntity<ApiResponse<CitaResponseDTO>> cancelarCita(@PathVariable Long citaId) {
        CitaResponseDTO citaCancelada = citaService.cancelarCita(citaId);
        ApiResponse<CitaResponseDTO> response = ApiResponse.success(citaCancelada, "Cita cancelada exitosamente.");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{citaId}/solicitar-cancelacion")
    public ResponseEntity<ApiResponse<CitaResponseDTO>> solicitarCancelacionCita(@PathVariable Long citaId, @Valid @RequestBody CitaCancelacionRequestDTO cancelacionRequestDTO) {
        CitaResponseDTO citaCancelada = citaService.solicitarCancelacionCita(citaId, cancelacionRequestDTO);
        ApiResponse<CitaResponseDTO> response = ApiResponse.success(citaCancelada, "Solicitud de cancelación de cita enviada exitosamente.");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{citaId}/reprogramar")
    public ResponseEntity<ApiResponse<CitaResponseDTO>> reprogramarCita(@PathVariable Long citaId, @Valid @RequestBody CitaReprogramarDTO reprogramarDTO) {
        CitaResponseDTO citaReprogramada = citaService.reprogramarCita(citaId, reprogramarDTO);
        ApiResponse<CitaResponseDTO> response = ApiResponse.success(citaReprogramada, "Solicitud de reprogramación enviada exitosamente.");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{citaId}/finalizar")
    public ResponseEntity<ApiResponse<CitaResponseDTO>> finalizarCita(@PathVariable Long citaId, @Valid @RequestBody FinalizarCitaDTO finalizarCitaDTO) {
        CitaResponseDTO citaFinalizada = citaService.finalizarCita(citaId, finalizarCitaDTO);
        ApiResponse<CitaResponseDTO> response = ApiResponse.success(citaFinalizada, "Cita marcada como finalizada.");
        return ResponseEntity.ok(response);
    }
}
