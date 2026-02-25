package com.upemor.app_nutriologo.alimento.service;

import com.upemor.app_nutriologo.alimento.dto.AlimentoRequestDTO;
import com.upemor.app_nutriologo.alimento.dto.AlimentoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AlimentoService {

    AlimentoResponseDTO registrarAlimento(AlimentoRequestDTO requestDTO, MultipartFile imagen, String correoUsuario) throws IOException;

    Page<AlimentoResponseDTO> listarAlimentos(Pageable pageable);

    List<AlimentoResponseDTO> buscarPorCategoria(String categoria);

    AlimentoResponseDTO buscarPorId(Long id);

    AlimentoResponseDTO actualizarAlimento(Long id, AlimentoRequestDTO requestDTO, MultipartFile imagen) throws IOException;

    void eliminarAlimento(Long id);
}
