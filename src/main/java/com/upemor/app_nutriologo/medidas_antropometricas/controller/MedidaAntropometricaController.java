package com.upemor.app_nutriologo.medidas_antropometricas.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.medidas_antropometricas.dto.MedidaAntropometricaRequestDTO;
import com.upemor.app_nutriologo.medidas_antropometricas.dto.MedidaAntropometricaResponseDTO;
import com.upemor.app_nutriologo.medidas_antropometricas.service.MedidaAntropometricaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/medidas")
public class MedidaAntropometricaController {

    private final MedidaAntropometricaService medidaService;

    @PostMapping("/{pacienteId}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<MedidaAntropometricaResponseDTO>> registrarMedida(
            @PathVariable Long pacienteId,
            @Valid @RequestBody MedidaAntropometricaRequestDTO requestDTO) {

        MedidaAntropometricaResponseDTO medida = medidaService.registrarMedida(pacienteId, requestDTO);
        ApiResponse<MedidaAntropometricaResponseDTO> response = ApiResponse.created(medida, "Medida antropométrica registrada exitosamente");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/historial/{pacienteId}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<List<MedidaAntropometricaResponseDTO>>> obtenerHistorial(
            @PathVariable Long pacienteId) {

        List<MedidaAntropometricaResponseDTO> historial = medidaService.obtenerHistorialPorPaciente(pacienteId);
        ApiResponse<List<MedidaAntropometricaResponseDTO>> response = ApiResponse.success(historial, "Historial de medidas obtenido exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ultima/{pacienteId}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<MedidaAntropometricaResponseDTO>> obtenerUltimaMedida(
            @PathVariable Long pacienteId) {

        MedidaAntropometricaResponseDTO ultimaMedida = medidaService.obtenerUltimaMedida(pacienteId);
        ApiResponse<MedidaAntropometricaResponseDTO> response = ApiResponse.success(ultimaMedida, "Última medida obtenida exitosamente");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{medidaId}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<MedidaAntropometricaResponseDTO>> actualizarMedida(
            @PathVariable Long medidaId,
            @Valid @RequestBody MedidaAntropometricaRequestDTO requestDTO) {

        MedidaAntropometricaResponseDTO medidaActualizada = medidaService.actualizarMedida(medidaId, requestDTO);
        ApiResponse<MedidaAntropometricaResponseDTO> response = ApiResponse.success(medidaActualizada, "Medida antropométrica actualizada exitosamente");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{medidaId}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<Void>> eliminarMedida(@PathVariable Long medidaId) {
        medidaService.eliminarMedida(medidaId);

        // Para DELETE, es común devolver 204 No Content sin cuerpo de respuesta.
        // Si se quiere devolver un mensaje, es mejor usar 200 OK.
        ApiResponse<Void> response = ApiResponse.success(null, "Medida eliminada exitosamente");
        return ResponseEntity.ok(response);
    }
}
