package com.upemor.app_nutriologo.usuario.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import com.upemor.app_nutriologo.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro")
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
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> listarUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarUsuarios();

        ApiResponse<List<UsuarioResponseDTO>> response = ApiResponse.<List<UsuarioResponseDTO>>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Listado de usuarios")
                .datos(usuarios)
                .build();

        return ResponseEntity.ok(response);
    }
}
