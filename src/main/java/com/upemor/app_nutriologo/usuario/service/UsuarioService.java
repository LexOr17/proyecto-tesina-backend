package com.upemor.app_nutriologo.usuario.service;

import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {

    UsuarioResponseDTO guardarUsuario(UsuarioRequestDTO usuarioRequestDTO);

    List<UsuarioResponseDTO> listarUsuarios();

    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO);

    void eliminarUsuario(Long id);
}
