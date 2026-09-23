package com.upemor.app_nutriologo.meta.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.meta.dto.MetaEstadoDTO;
import com.upemor.app_nutriologo.meta.dto.MetaRequestDTO;
import com.upemor.app_nutriologo.meta.dto.MetaResponseDTO;
import com.upemor.app_nutriologo.meta.dto.MetaStatusCountDTO;
import com.upemor.app_nutriologo.meta.service.ServiceMeta;
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
@RequestMapping("/metas")
@RequiredArgsConstructor
public class MetaController {

    private final ServiceMeta metaService;

    @PostMapping
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<MetaResponseDTO>> crearMeta(
            @Valid @RequestBody MetaRequestDTO metaDTO) {
        MetaResponseDTO nuevaMeta = metaService.crearMeta(metaDTO);
        return new ResponseEntity<>(ApiResponse.created(nuevaMeta, "Meta creada exitosamente"), HttpStatus.CREATED);
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<Page<MetaResponseDTO>>> listarMetasPorPaciente(
            @PathVariable Long pacienteId,
            @PageableDefault(size = 10, sort = "fechaObjetivo") Pageable pageable) {

        Page<MetaResponseDTO> metas = metaService.listarMetasPorPaciente(pacienteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(metas, "Metas listadas exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<MetaResponseDTO>> buscarMeta(@PathVariable Long id) {
        MetaResponseDTO meta = metaService.buscarMeta(id);
        return ResponseEntity.ok(ApiResponse.success(meta, "Meta encontrada"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<MetaResponseDTO>> actualizarMeta(
            @PathVariable Long id, @Valid @RequestBody MetaRequestDTO metaDTO) {
        MetaResponseDTO metaActualizada = metaService.actualizarMeta(id, metaDTO);
        return ResponseEntity.ok(ApiResponse.success(metaActualizada, "Meta actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<Void>> eliminarMeta(@PathVariable Long id) {
        metaService.eliminarMeta(id);
        return ResponseEntity.ok(ApiResponse.noContent("Meta eliminada exitosamente"));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('PACIENTE', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<MetaResponseDTO>> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody MetaEstadoDTO estadoDTO) {

        MetaResponseDTO actualizada = metaService.actualizarEstado(id, estadoDTO);
        return ResponseEntity.ok(ApiResponse.success(actualizada, "Estado actualizado con éxito"));
    }

    @GetMapping("/paciente/{pacienteId}/resumen")
    @PreAuthorize("hasAnyRole('PACIENTE', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<MetaStatusCountDTO>> getResumenMetas(@PathVariable Long pacienteId) {
        MetaStatusCountDTO resumen = metaService.obtenerConteoMetas(pacienteId);
        return ResponseEntity.ok(ApiResponse.success(resumen, "Resumen estadístico generado con éxito"));
    }
}