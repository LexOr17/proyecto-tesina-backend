package com.upemor.app_nutriologo.meta.service;

import com.upemor.app_nutriologo.meta.dto.MetaRequestDTO;
import com.upemor.app_nutriologo.meta.dto.MetaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceMeta {

    MetaResponseDTO crearMeta(MetaRequestDTO metaDTO);

    Page<MetaResponseDTO> listarMetas(Pageable pageable);

    MetaResponseDTO buscarMeta(Integer id);

    MetaResponseDTO actualizarMeta(Integer id, MetaRequestDTO metaDTO);

    void eliminarMeta(Integer id);

    MetaResponseDTO marcarComoCumplida(Integer id);
}
