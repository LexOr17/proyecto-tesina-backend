package com.upemor.app_nutriologo.meta.service;

import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.meta.dto.MetaRequestDTO;
import com.upemor.app_nutriologo.meta.dto.MetaResponseDTO;
import com.upemor.app_nutriologo.meta.entity.Meta;
import com.upemor.app_nutriologo.meta.mapper.MetaMapper;
import com.upemor.app_nutriologo.meta.repository.MetaRepository;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
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
public class ServiceMetaImpl implements ServiceMeta { // Cambiado para seguir la convención

    private final MetaRepository metaRepository;
    private final PacienteRepository pacienteRepository;
    // Nota: Ya no necesitamos inyectar NutriologoRepository porque obtenemos el nutriologo desde el Paciente
    private final UserContextService userContextService;
    private final MetaMapper metaMapper;

    @Override
    @Transactional
    public MetaResponseDTO crearMeta(MetaRequestDTO metaDTO) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();

        Paciente paciente = pacienteRepository.findById(metaDTO.pacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente con el id: " + metaDTO.pacienteId()));

        // Validación anti-NullPointer
        if (paciente.getNutriologo() == null || !paciente.getNutriologo().getId().equals(idNutriologoLogueado)) {
            throw new AccessDeniedException("No tienes permiso para asignar metas a este paciente.");
        }

        Meta meta = metaMapper.toEntity(metaDTO);
        meta.setPaciente(paciente); // Aseguramos que la entidad sepa quién es su paciente
        meta.setNutriologo(paciente.getNutriologo());

        return metaMapper.toResponseDto(metaRepository.save(meta));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MetaResponseDTO> listarMetasPorPaciente(Long pacienteId, Pageable pageable) {
        Long usuarioAutenticadoId = userContextService.getCurrentUserId();

        // Verificamos si es el propio paciente consultando sus metas
        boolean esElPropioPaciente = usuarioAutenticadoId.equals(pacienteId);

        // Si no es el paciente, verificamos que sea su nutriólogo asignado
        if (!esElPropioPaciente) {
            Paciente paciente = pacienteRepository.findById(pacienteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

            if (paciente.getNutriologo() == null || !paciente.getNutriologo().getId().equals(usuarioAutenticadoId)) {
                throw new AccessDeniedException("No tienes permisos para ver las metas de este paciente.");
            }
        }

        // Usamos el método que creamos en el Repositorio
        return metaRepository.findByPacienteId(pacienteId, pageable)
                .map(metaMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public MetaResponseDTO buscarMeta(Integer id) {
        Long currentUserId = userContextService.getCurrentUserId();

        return metaRepository.findById(id)
                .map(meta -> {
                    // Tanto el paciente como su nutriólogo pueden ver el detalle de la meta
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
                    // Usamos la sintaxis de Records para obtener los datos
                    metaMapper.updateEntityFromDto(metaDTO, meta);
                    return metaMapper.toResponseDto(metaRepository.save(meta));
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la meta con el id: " + id));
    }

    @Override
    @Transactional
    public void eliminarMeta(Integer id) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();

        // Ahora sí lanzará un 404 si el ID no existe
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la meta con el id: " + id));

        if (!meta.getNutriologo().getId().equals(idNutriologoLogueado)) {
            throw new AccessDeniedException("No tienes permiso para eliminar esta meta.");
        }

        metaRepository.delete(meta);
    }

    @Override
    @Transactional
    public MetaResponseDTO marcarComoCumplida(Integer id) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();

        return metaRepository.findById(id)
                .map(meta -> {
                    // ¿Regla de negocio extra?: ¿Te gustaría que el paciente también pudiera marcar su propia meta como cumplida?
                    if (!meta.getNutriologo().getId().equals(idNutriologoLogueado)) {
                        throw new AccessDeniedException("No tienes permiso para actualizar esta meta.");
                    }
                    meta.setCumplida(true);
                    return metaMapper.toResponseDto(metaRepository.save(meta));
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la meta con el id: " + id));
    }
}