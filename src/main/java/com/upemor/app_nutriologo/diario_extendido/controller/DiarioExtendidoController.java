package com.upemor.app_nutriologo.diario_extendido.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoNutriologoRequestDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoPacienteRequestDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoResponseDTO;
import com.upemor.app_nutriologo.diario_extendido.service.DiarioExtendidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/diario-extendido")
@RequiredArgsConstructor
public class DiarioExtendidoController {

    private final DiarioExtendidoService diarioService;

    // --- ENDPOINTS PARA EL PACIENTE ---

    /**
     * El paciente registra o actualiza su diario del día de hoy.
     */
    @PostMapping
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<DiarioExtendidoResponseDTO>> registrarDiario(
            @Valid @RequestBody DiarioExtendidoPacienteRequestDTO dto) {
        var response = diarioService.registrarOActualizarDiario(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Diario de hoy registrado correctamente."));
    }

    /**
     * El paciente consulta su propio diario por una fecha específica.
     */
    @GetMapping("/mi-diario")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<DiarioExtendidoResponseDTO>> obtenerMiDiarioPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        // Pasamos null como pacienteId para que el Service use el ID del token
        var response = diarioService.buscarDiarioPorFecha(null, fecha);
        return ResponseEntity.ok(ApiResponse.success(response, "Diario recuperado con éxito."));
    }

    // --- ENDPOINTS PARA EL NUTRIÓLOGO ---

    /**
     * El nutriólogo programa una sugerencia de bienestar (hoy o fecha futura).
     */
    @PatchMapping("/pacientes/{pacienteId}/sugerencia")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<DiarioExtendidoResponseDTO>> programarSugerencia(
            @PathVariable Long pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @Valid @RequestBody DiarioExtendidoNutriologoRequestDTO dto) {

        var response = diarioService.sugerirActividad(pacienteId, fecha, dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Sugerencia programada correctamente para el paciente."));
    }

    /**
     * El nutriólogo consulta el historial completo de un paciente de forma paginada.
     */
    @GetMapping("/pacientes/{pacienteId}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO','PACIENTE','ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Page<DiarioExtendidoResponseDTO>>> listarHistorialPaciente(
            @PathVariable Long pacienteId,
            @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {

        var response = diarioService.listarDiariosDePaciente(pacienteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Historial obtenido con éxito."));
    }

    // --- ENDPOINTS ADMINISTRATIVOS ---

    /**
     * Solo un administrador puede eliminar registros permanentemente.
     */
    @DeleteMapping("/{diarioId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminarDiario(@PathVariable Long diarioId) {
        diarioService.eliminarDiario(diarioId);
        return ResponseEntity.ok(ApiResponse.success(null, "El registro ha sido eliminado por el administrador."));
    }
}