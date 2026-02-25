package com.upemor.app_nutriologo.alimento.controller;

import com.upemor.app_nutriologo.alimento.dto.AlimentoRequestDTO;
import com.upemor.app_nutriologo.alimento.dto.AlimentoResponseDTO;
import com.upemor.app_nutriologo.alimento.service.AlimentoService;
import com.upemor.app_nutriologo.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alimentos")
public class AlimentoController {

    private final AlimentoService alimentoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<AlimentoResponseDTO>> registrarAlimento(
            @Valid @RequestPart("datos") AlimentoRequestDTO requestDTO,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen,
            Authentication authentication) throws IOException {

        String correoUsuario = authentication.getName();
        AlimentoResponseDTO nuevoAlimento = alimentoService.registrarAlimento(requestDTO, imagen, correoUsuario);

        ApiResponse<AlimentoResponseDTO> response = ApiResponse.<AlimentoResponseDTO>builder()
                .estatus(HttpStatus.CREATED.value())
                .mensaje("Alimento registrado exitosamente")
                .datos(nuevoAlimento)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<Page<AlimentoResponseDTO>>> listarAlimentos(Pageable pageable) {
        Page<AlimentoResponseDTO> alimentos = alimentoService.listarAlimentos(pageable);

        ApiResponse<Page<AlimentoResponseDTO>> response = ApiResponse.<Page<AlimentoResponseDTO>>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Listado de alimentos")
                .datos(alimentos)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<List<AlimentoResponseDTO>>> buscarPorCategoria(@PathVariable String categoria) {
        List<AlimentoResponseDTO> alimentos = alimentoService.buscarPorCategoria(categoria);

        ApiResponse<List<AlimentoResponseDTO>> response = ApiResponse.<List<AlimentoResponseDTO>>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Alimentos encontrados en la categoría: " + categoria)
                .datos(alimentos)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<AlimentoResponseDTO>> buscarPorId(@PathVariable Long id) {
        AlimentoResponseDTO alimento = alimentoService.buscarPorId(id);

        ApiResponse<AlimentoResponseDTO> response = ApiResponse.<AlimentoResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Alimento encontrado")
                .datos(alimento)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<AlimentoResponseDTO>> actualizarAlimento(
            @PathVariable Long id,
            @Valid @RequestPart("datos") AlimentoRequestDTO requestDTO,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) throws IOException {

        AlimentoResponseDTO alimentoActualizado = alimentoService.actualizarAlimento(id, requestDTO, imagen);

        ApiResponse<AlimentoResponseDTO> response = ApiResponse.<AlimentoResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Alimento actualizado exitosamente")
                .datos(alimentoActualizado)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> eliminarAlimento(@PathVariable Long id) {
        alimentoService.eliminarAlimento(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .estatus(HttpStatus.NO_CONTENT.value())
                .mensaje("Alimento eliminado exitosamente")
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
