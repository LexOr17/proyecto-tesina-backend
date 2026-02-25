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

        ApiResponse<MedidaAntropometricaResponseDTO> response = ApiResponse.<MedidaAntropometricaResponseDTO>builder()
                .estatus(HttpStatus.CREATED.value())
                .mensaje("Medida antropométrica registrada exitosamente")
                .datos(medida)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/historial/{pacienteId}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<List<MedidaAntropometricaResponseDTO>>> obtenerHistorial(
            @PathVariable Long pacienteId) {

        List<MedidaAntropometricaResponseDTO> historial = medidaService.obtenerHistorialPorPaciente(pacienteId);

        ApiResponse<List<MedidaAntropometricaResponseDTO>> response = ApiResponse.<List<MedidaAntropometricaResponseDTO>>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Historial de medidas obtenido exitosamente")
                .datos(historial)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ultima/{pacienteId}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<MedidaAntropometricaResponseDTO>> obtenerUltimaMedida(
            @PathVariable Long pacienteId) {

        MedidaAntropometricaResponseDTO ultimaMedida = medidaService.obtenerUltimaMedida(pacienteId);

        ApiResponse<MedidaAntropometricaResponseDTO> response = ApiResponse.<MedidaAntropometricaResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Última medida obtenida exitosamente")
                .datos(ultimaMedida)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{medidaId}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<MedidaAntropometricaResponseDTO>> actualizarMedida(
            @PathVariable Long medidaId,
            @Valid @RequestBody MedidaAntropometricaRequestDTO requestDTO) {

        MedidaAntropometricaResponseDTO medidaActualizada = medidaService.actualizarMedida(medidaId, requestDTO);

        ApiResponse<MedidaAntropometricaResponseDTO> response = ApiResponse.<MedidaAntropometricaResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Medida antropométrica actualizada exitosamente")
                .datos(medidaActualizada)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{medidaId}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<Void>> eliminarMedida(@PathVariable Long medidaId) {
        medidaService.eliminarMedida(medidaId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .estatus(HttpStatus.NO_CONTENT.value())
                .mensaje("Medida eliminada exitosamente")
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
