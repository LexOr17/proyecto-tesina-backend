package com.upemor.app_nutriologo.usuario.service;

import com.upemor.app_nutriologo.usuario.dto.UsuarioNombreDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {

    UsuarioResponseDTO guardarUsuario(UsuarioRequestDTO usuarioRequestDTO);

    Page<UsuarioResponseDTO> listarUsuarios(Pageable pageable);

    Page<UsuarioResponseDTO> buscarUsuariosPorPalabraClave(String keyword, Pageable pageable);

    UsuarioResponseDTO buscarUsuarioPorId(Long id);
    
    UsuarioNombreDTO obtenerNombreUsuarioPorId(Long id);

    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO);

    void eliminarUsuario(Long id);
}
