package com.upemor.app_nutriologo.usuario.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import com.upemor.app_nutriologo.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> crearUsuario(@Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO nuevoUsuario = usuarioService.guardarUsuario(usuarioRequestDTO);

        ApiResponse<UsuarioResponseDTO> response = ApiResponse.<UsuarioResponseDTO>builder()
                .estatus(HttpStatus.CREATED.value())
                .mensaje("Usuario creado exitosamente")
                .datos(nuevoUsuario)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UsuarioResponseDTO>>> listarUsuarios(Pageable pageable) {
        Page<UsuarioResponseDTO> usuarios = usuarioService.listarUsuarios(pageable);

        ApiResponse<Page<UsuarioResponseDTO>> response = ApiResponse.<Page<UsuarioResponseDTO>>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Listado de usuarios")
                .datos(usuarios)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> obtenerUsuarioPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.buscarUsuarioPorId(id);

        ApiResponse<UsuarioResponseDTO> response = ApiResponse.<UsuarioResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Usuario encontrado")
                .datos(usuario)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioRequestDTO);

        ApiResponse<UsuarioResponseDTO> response = ApiResponse.<UsuarioResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Usuario actualizado exitosamente")
                .datos(usuarioActualizado)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .estatus(HttpStatus.NO_CONTENT.value())
                .mensaje("Usuario eliminado exitosamente")
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
