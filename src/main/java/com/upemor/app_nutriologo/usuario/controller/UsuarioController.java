package com.upemor.app_nutriologo.usuario.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.usuario.dto.UsuarioNombreDTO;
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(nuevoUsuario, "Usuario creado exitosamente"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Page<UsuarioResponseDTO>>> listarUsuarios(Pageable pageable) {
        Page<UsuarioResponseDTO> usuarios = usuarioService.listarUsuarios(pageable);
        return ResponseEntity.ok(ApiResponse.success(usuarios, "Listado de usuarios"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> obtenerUsuarioPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(ApiResponse.success(usuario, "Usuario encontrado"));
    }

    @GetMapping("/{id}/nombre")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<UsuarioNombreDTO>> obtenerNombreUsuarioPorId(@PathVariable Long id) {
        UsuarioNombreDTO usuarioNombre = usuarioService.obtenerNombreUsuarioPorId(id);
        return ResponseEntity.ok(ApiResponse.success(usuarioNombre, "Nombre de usuario encontrado"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioRequestDTO);
        return ResponseEntity.ok(ApiResponse.success(usuarioActualizado, "Usuario actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
