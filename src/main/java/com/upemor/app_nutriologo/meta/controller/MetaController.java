package com.upemor.app_nutriologo.meta.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.meta.dto.MetaRequestDTO;
import com.upemor.app_nutriologo.meta.dto.MetaResponseDTO;
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

    private final ServiceMeta metaService; // Nombre actualizado a la convención

    @PostMapping
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<MetaResponseDTO>> crearMeta(
            @Valid @RequestBody MetaRequestDTO metaDTO) {

        MetaResponseDTO nuevaMeta = metaService.crearMeta(metaDTO);

        // ¡Mira qué limpio queda usando tu método estático!
        return new ResponseEntity<>(ApiResponse.created(nuevaMeta, "Meta creada exitosamente"), HttpStatus.CREATED);
    }

    // --- ENDPOINT CORREGIDO PARA LISTAR POR PACIENTE ---
    @GetMapping("/pacientes/{pacienteId}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'NUTRIOLOGO')") // Ambos pueden ver la lista
    public ResponseEntity<ApiResponse<Page<MetaResponseDTO>>> listarMetasPorPaciente(
            @PathVariable Long pacienteId,
            @PageableDefault(size = 10, sort = "fechaObjetivo") Pageable pageable) { // Default seguro

        Page<MetaResponseDTO> metas = metaService.listarMetasPorPaciente(pacienteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(metas, "Metas listadas exitosamente"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'NUTRIOLOGO')") // Seguridad de capa externa agregada
    public ResponseEntity<ApiResponse<MetaResponseDTO>> buscarMeta(@PathVariable Integer id) {

        MetaResponseDTO meta = metaService.buscarMeta(id);
        return ResponseEntity.ok(ApiResponse.success(meta, "Meta encontrada"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<MetaResponseDTO>> actualizarMeta(
            @PathVariable Integer id, @Valid @RequestBody MetaRequestDTO metaDTO) {

        MetaResponseDTO metaActualizada = metaService.actualizarMeta(id, metaDTO);
        return ResponseEntity.ok(ApiResponse.success(metaActualizada, "Meta actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<Void>> eliminarMeta(@PathVariable Integer id) {

        metaService.eliminarMeta(id);

        // Estandarizamos la respuesta de borrado usando tu ApiResponse
        return ResponseEntity.ok(ApiResponse.noContent("Meta eliminada exitosamente"));
    }

    @PatchMapping("/{id}/cumplir")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    // OPCIONAL: Si decides que el paciente también puede marcarla cumplida, cambia a hasAnyRole('PACIENTE', 'NUTRIOLOGO')
    public ResponseEntity<ApiResponse<MetaResponseDTO>> marcarComoCumplida(@PathVariable Integer id) {

        MetaResponseDTO metaActualizada = metaService.marcarComoCumplida(id);
        return ResponseEntity.ok(ApiResponse.success(metaActualizada, "Meta marcada como cumplida"));
    }
}