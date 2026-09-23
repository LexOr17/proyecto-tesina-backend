package com.upemor.app_nutriologo.meta.service;

import com.upemor.app_nutriologo.meta.dto.MetaEstadoDTO;
import com.upemor.app_nutriologo.meta.dto.MetaRequestDTO;
import com.upemor.app_nutriologo.meta.dto.MetaResponseDTO;
import com.upemor.app_nutriologo.meta.dto.MetaStatusCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceMeta {

    MetaResponseDTO crearMeta(MetaRequestDTO metaDTO);

    Page<MetaResponseDTO> listarMetasPorPaciente(Long pacienteId, Pageable pageable);

    MetaResponseDTO buscarMeta(Long id);

    MetaResponseDTO actualizarMeta(Long id, MetaRequestDTO metaDTO);

    void eliminarMeta(Long id);

    MetaResponseDTO actualizarEstado(Long id, MetaEstadoDTO dto);

    MetaStatusCountDTO obtenerConteoMetas(Long pacienteId);

}
