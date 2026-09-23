package com.upemor.app_nutriologo.meta.service;

import com.upemor.app_nutriologo.common.exceptions.BadRequestException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.meta.dto.MetaEstadoDTO;
import com.upemor.app_nutriologo.meta.dto.MetaRequestDTO;
import com.upemor.app_nutriologo.meta.dto.MetaResponseDTO;
import com.upemor.app_nutriologo.meta.dto.MetaStatusCountDTO;
import com.upemor.app_nutriologo.meta.entity.Meta;
import com.upemor.app_nutriologo.meta.enums.EstadoMeta;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceMetaImpl implements ServiceMeta {

    private final MetaRepository metaRepository;
    private final PacienteRepository pacienteRepository;
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
    public MetaResponseDTO buscarMeta(Long id) {
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
    public MetaResponseDTO actualizarMeta(Long id, MetaRequestDTO metaDTO) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la meta con el id: " + id));

        validarPropiedadNutriologo(meta);

        metaMapper.updateEntityFromDto(metaDTO, meta);
        return metaMapper.toResponseDto(metaRepository.save(meta));
    }

    @Override
    @Transactional
    public void eliminarMeta(Long id) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la meta con el id: " + id));

        validarPropiedadNutriologo(meta);

        metaRepository.delete(meta);
    }

    @Override
    @Transactional
    public MetaResponseDTO actualizarEstado(Long id, MetaEstadoDTO dto) {
        Meta meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrada"));

        // 1. Evitar cambios si la meta ya está finalizada (Opcional, según tu regla de negocio)
        if (meta.getEstado() == EstadoMeta.CUMPLIDA) {
            throw new BadRequestException("No se puede cambiar el estado de una meta que ya ha sido cumplida.");
        }

        // 2. Obtener roles de forma consistente
        boolean isNutriologo = userContextService.getCurrentUserRoles().contains("ROLE_NUTRIOLOGO");
        boolean isPaciente = userContextService.getCurrentUserRoles().contains("ROLE_PACIENTE");

        // 3. Switch centralizado de seguridad
        switch (dto.nuevoEstado()) {
            case POR_VALIDAR, NO_CUMPLIDA -> {
                if (isPaciente) {
                    validarPropiedadPaciente(meta);
                } else if (isNutriologo) {
                    validarPropiedadNutriologo(meta);
                } else {
                    throw new AccessDeniedException("No tiene permisos para modificar esta meta");
                }
            }
            case CUMPLIDA, RECHAZADA -> {
                if (!isNutriologo) throw new AccessDeniedException("Acción exclusiva del nutriólogo");
                validarPropiedadNutriologo(meta);
            }
            default -> throw new BadRequestException("Transición de estado no válida");
        }

        meta.setEstado(dto.nuevoEstado());
        meta.setObservaciones(dto.observaciones());

        return metaMapper.toResponseDto(metaRepository.save(meta));
    }


    private void validarPropiedadNutriologo(Meta meta) {
        Long idLogueado = userContextService.getCurrentUserId();
        if (!meta.getNutriologo().getId().equals(idLogueado)) {
            throw new AccessDeniedException("No tienes permiso. Esta meta pertenece a otro nutriólogo.");
        }
    }

    private void validarPropiedadPaciente(Meta meta) {
        Long idLogueado = userContextService.getCurrentUserId();
        if (!meta.getPaciente().getId().equals(idLogueado)) {
            throw new AccessDeniedException("No tienes permiso. Esta meta no es tuya.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MetaStatusCountDTO obtenerConteoMetas(Long pacienteId) {
        List<Object[]> resultados = metaRepository.countMetasByStatusForPaciente(pacienteId);

        long pendientes = 0, porValidar = 0, cumplidas = 0, rechazadas = 0, noCumplidas = 0;

        for (Object[] fila : resultados) {
            EstadoMeta estado = (EstadoMeta) fila[0];
            long conteo = (long) fila[1];

            switch (estado) {
                case PENDIENTE -> pendientes = conteo;
                case POR_VALIDAR -> porValidar = conteo;
                case CUMPLIDA -> cumplidas = conteo;
                case RECHAZADA -> rechazadas = conteo;
                case NO_CUMPLIDA -> noCumplidas = conteo;
            }
        }

        long total = pendientes + porValidar + cumplidas + rechazadas + noCumplidas;

        // Cálculo del porcentaje evitando división por cero
        double porcentaje = 0.0;
        if (total > 0) {
            porcentaje = (double) cumplidas / total * 100;
            // Redondear a 2 decimales si lo deseas
            porcentaje = Math.round(porcentaje * 100.0) / 100.0;
        }

        return new MetaStatusCountDTO(
                pendientes,
                porValidar,
                cumplidas,
                rechazadas,
                noCumplidas,
                total,
                porcentaje
        );
    }
}