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
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/diarios-extendidos")
@RequiredArgsConstructor
public class DiarioExtendidoController {

    private final DiarioExtendidoService diarioService;

    @PostMapping
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<DiarioExtendidoResponseDTO>> registrarDiario(
            @Valid @RequestBody DiarioExtendidoPacienteRequestDTO dto) {
        DiarioExtendidoResponseDTO responseDto = diarioService.registrarOActualizarDiario(dto);
        // Nota: Como este endpoint hace "Upsert" (Crea o Actualiza), HttpStatus.CREATED o OK son válidos.
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.CREATED.value(), "Diario registrado/actualizado con éxito", responseDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{diarioId}/observaciones")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<DiarioExtendidoResponseDTO>> agregarObservaciones(
            @PathVariable Integer diarioId,
            @Valid @RequestBody DiarioExtendidoNutriologoRequestDTO dto) { // Agregamos @Valid por precaución futura
        DiarioExtendidoResponseDTO responseDto = diarioService.agregarObservacionesNutriologo(diarioId, dto);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Observaciones del nutriólogo agregadas", responseDto));
    }

    @GetMapping("/fecha")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<DiarioExtendidoResponseDTO>> obtenerDiarioPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        DiarioExtendidoResponseDTO responseDto = diarioService.buscarDiarioPorFecha(fecha);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Diario encontrado", responseDto));
    }

    // --- NUEVO ENDPOINT ---
    @GetMapping("/pacientes/{pacienteId}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'NUTRIOLOGO')") // Ambos roles pueden intentar acceder
    public ResponseEntity<ApiResponse<Page<DiarioExtendidoResponseDTO>>> listarDiariosDePaciente(
            @PathVariable Long pacienteId,
            @PageableDefault(size = 10, sort = "fecha") Pageable pageable) {

        // La validación de seguridad fuerte (si el nutriólogo es el asignado al paciente)
        // ya la programamos de manera segura en la capa Service.
        Page<DiarioExtendidoResponseDTO> diariosPage = diarioService.listarDiariosDePaciente(pacienteId, pageable);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Historial de diarios obtenido", diariosPage));
    }

    @DeleteMapping("/{diarioId}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<Void>> eliminarDiario(@PathVariable Integer diarioId) {
        diarioService.eliminarDiario(diarioId);

        // Usamos tu método estático para respuestas sin contenido (HTTP 204 No Content / HTTP 200 OK)
        return ResponseEntity.ok(ApiResponse.noContent("Diario eliminado con éxito"));
    }
}