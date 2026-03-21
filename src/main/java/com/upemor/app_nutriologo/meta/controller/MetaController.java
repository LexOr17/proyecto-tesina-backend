package com.upemor.app_nutriologo.meta.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.meta.dto.MetaRequestDTO;
import com.upemor.app_nutriologo.meta.dto.MetaResponseDTO;
import com.upemor.app_nutriologo.meta.service.ServiceMeta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/metas")
@RequiredArgsConstructor
public class MetaController {

    private final ServiceMeta serviceMeta;

    @PostMapping
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<MetaResponseDTO>> crearMeta(
            @Valid @RequestBody MetaRequestDTO metaDTO) {
        MetaResponseDTO nuevaMeta = serviceMeta.crearMeta(metaDTO);
        ApiResponse<MetaResponseDTO> response = ApiResponse.<MetaResponseDTO>builder()
                .estatus(HttpStatus.CREATED.value())
                .mensaje("Meta creada exitosamente")
                .datos(nuevaMeta)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MetaResponseDTO>>> listarMetas(Pageable pageable) {
        Page<MetaResponseDTO> metas = serviceMeta.listarMetas(pageable);
        ApiResponse<Page<MetaResponseDTO>> response = ApiResponse.<Page<MetaResponseDTO>>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Metas listadas exitosamente")
                .datos(metas)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MetaResponseDTO>> buscarMeta(@PathVariable Integer id) {
        MetaResponseDTO meta = serviceMeta.buscarMeta(id);
        ApiResponse<MetaResponseDTO> response = ApiResponse.<MetaResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Meta encontrada")
                .datos(meta)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<MetaResponseDTO>> actualizarMeta(
            @PathVariable Integer id, @Valid @RequestBody MetaRequestDTO metaDTO) {
        MetaResponseDTO metaActualizada = serviceMeta.actualizarMeta(id, metaDTO);
        ApiResponse<MetaResponseDTO> response = ApiResponse.<MetaResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Meta actualizada exitosamente")
                .datos(metaActualizada)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<Void> eliminarMeta(@PathVariable Integer id) {
        serviceMeta.eliminarMeta(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cumplir")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<MetaResponseDTO>> marcarComoCumplida(@PathVariable Integer id) {
        MetaResponseDTO metaActualizada = serviceMeta.marcarComoCumplida(id);
        ApiResponse<MetaResponseDTO> response = ApiResponse.<MetaResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Meta marcada como cumplida")
                .datos(metaActualizada)
                .build();
        return ResponseEntity.ok(response);
    }
}
