package com.upemor.app_nutriologo.seguimiento.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.seguimiento.dto.*;
import com.upemor.app_nutriologo.seguimiento.service.SeguimientoConsultaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/seguimiento")
@RequiredArgsConstructor
public class SeguimientoController {

    private final SeguimientoConsultaService seguimientoService;

    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')")
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<ApiResponse<Page<SeguimientoConsultaDTO>>> listarPorPaciente(
            @PathVariable Long pacienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SeguimientoConsultaDTO> resultado = seguimientoService.listarPorPaciente(pacienteId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.<Page<SeguimientoConsultaDTO>>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Seguimientos del paciente obtenidos con éxito.")
                .datos(resultado)
                .build());
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/mis-seguimientos")
    public ResponseEntity<ApiResponse<Page<SeguimientoConsultaDTO>>> misSeguimientos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SeguimientoConsultaDTO> resultado = seguimientoService.listarMisSeguimientos(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.<Page<SeguimientoConsultaDTO>>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Tus seguimientos se obtuvieron con éxito.")
                .datos(resultado)
                .build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/todos")
    public ResponseEntity<ApiResponse<Page<SeguimientoConsultaDTO>>> listarTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SeguimientoConsultaDTO> resultado = seguimientoService.listarTodos(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.<Page<SeguimientoConsultaDTO>>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Todos los seguimientos obtenidos con éxito.")
                .datos(resultado)
                .build());
    }

    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'PACIENTE', 'ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SeguimientoConsultaDTO>> obtenerPorId(@PathVariable Long id) {
        SeguimientoConsultaDTO resultado = seguimientoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.<SeguimientoConsultaDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Seguimiento obtenido con éxito.")
                .datos(resultado)
                .build());
    }

    @PreAuthorize("hasRole('NUTRIOLOGO')")
    @GetMapping("/preview")
    public ResponseEntity<ApiResponse<SeguimientoPreviewDTO>> preview(
            @RequestParam Long pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        SeguimientoPreviewDTO preview = seguimientoService.generarPreview(pacienteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(ApiResponse.<SeguimientoPreviewDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Vista previa generada con éxito.")
                .datos(preview)
                .build());
    }

    @PreAuthorize("hasRole('NUTRIOLOGO')")
    @PostMapping
    public ResponseEntity<ApiResponse<SeguimientoConsultaDTO>> crear(
            @Valid @RequestBody CrearSeguimientoRequestDTO request) {
        SeguimientoConsultaDTO nuevo = seguimientoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<SeguimientoConsultaDTO>builder()
                .estatus(HttpStatus.CREATED.value())
                .mensaje("Consulta de seguimiento registrada correctamente.")
                .datos(nuevo)
                .build());
    }

    @PreAuthorize("hasRole('NUTRIOLOGO')")
    @PutMapping("/{id}/observaciones")
    public ResponseEntity<ApiResponse<SeguimientoConsultaDTO>> editarObservaciones(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarObservacionesDTO request) {
        SeguimientoConsultaDTO actualizado = seguimientoService.editarObservaciones(id, request);
        return ResponseEntity.ok(ApiResponse.<SeguimientoConsultaDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Observaciones actualizadas con éxito.")
                .datos(actualizado)
                .build());
    }

    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        seguimientoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Seguimiento eliminado correctamente.")
                .datos(null)
                .build());
    }
}
