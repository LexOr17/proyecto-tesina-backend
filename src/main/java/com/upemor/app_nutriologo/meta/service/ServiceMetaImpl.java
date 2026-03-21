package com.upemor.app_nutriologo.meta.service;

import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.meta.dto.MetaRequestDTO;
import com.upemor.app_nutriologo.meta.dto.MetaResponseDTO;
import com.upemor.app_nutriologo.meta.mapper.MetaMapper;
import com.upemor.app_nutriologo.meta.repository.MetaRepository;
import com.upemor.app_nutriologo.nutriologo.repository.NutriologoRepository;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import com.upemor.app_nutriologo.security.UserContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServiceMetaImpl implements ServiceMeta {

    private final MetaRepository metaRepository;
    private final PacienteRepository pacienteRepository;
    private final NutriologoRepository nutriologoRepository;
    private final UserContextService userContextService;
    private final MetaMapper metaMapper;

    @Override
    @Transactional
    public MetaResponseDTO crearMeta(MetaRequestDTO metaDTO) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();

        return pacienteRepository.findById(metaDTO.getPacienteId())
                .map(paciente -> {
                    if (!paciente.getNutriologo().getId().equals(idNutriologoLogueado)) {
                        throw new AccessDeniedException("No tienes permiso para asignar metas a este paciente.");
                    }
                    var meta = metaMapper.toEntity(metaDTO);
                    meta.setNutriologo(paciente.getNutriologo());
                    return metaMapper.toResponseDto(metaRepository.save(meta));
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente con el id: " + metaDTO.getPacienteId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MetaResponseDTO> listarMetas(Pageable pageable) {
        // Implementar la lógica para listar metas, posiblemente filtrando por paciente o nutriólogo
        return metaRepository.findAll(pageable).map(metaMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public MetaResponseDTO buscarMeta(Integer id) {
        Long currentUserId = userContextService.getCurrentUserId();
        return metaRepository.findById(id)
                .map(meta -> {
                    if (!meta.getNutriologo().getId().equals(currentUserId) && !meta.getPaciente().getId().equals(currentUserId)) {
                        throw new AccessDeniedException("No tienes permiso para ver esta meta.");
                    }
                    return metaMapper.toResponseDto(meta);
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la meta con el id: " + id));
    }

    @Override
    @Transactional
    public MetaResponseDTO actualizarMeta(Integer id, MetaRequestDTO metaDTO) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();
        return metaRepository.findById(id)
                .map(meta -> {
                    if (!meta.getNutriologo().getId().equals(idNutriologoLogueado)) {
                        throw new AccessDeniedException("No tienes permiso para actualizar esta meta.");
                    }
                    meta.setDescripcion(metaDTO.getDescripcion());
                    meta.setFechaObjetivo(metaDTO.getFechaObjetivo());
                    return metaMapper.toResponseDto(metaRepository.save(meta));
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la meta con el id: " + id));
    }

    @Override
    @Transactional
    public void eliminarMeta(Integer id) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();
        metaRepository.findById(id)
                .ifPresent(meta -> {
                    if (!meta.getNutriologo().getId().equals(idNutriologoLogueado)) {
                        throw new AccessDeniedException("No tienes permiso para eliminar esta meta.");
                    }
                    metaRepository.delete(meta);
                });
    }

    @Override
    @Transactional
    public MetaResponseDTO marcarComoCumplida(Integer id) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();
        return metaRepository.findById(id)
                .map(meta -> {
                    if (!meta.getNutriologo().getId().equals(idNutriologoLogueado)) {
                        throw new AccessDeniedException("No tienes permiso para actualizar esta meta.");
                    }
                    meta.setCumplida(true);
                    return metaMapper.toResponseDto(metaRepository.save(meta));
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la meta con el id: " + id));
    }
}
