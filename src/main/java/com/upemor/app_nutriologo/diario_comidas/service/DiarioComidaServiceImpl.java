package com.upemor.app_nutriologo.diario_comidas.service;

import com.upemor.app_nutriologo.common.exceptions.DuplicateResourceException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.common.service.DiarioVentanaValidator;
import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaRequestDTO;
import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaResponseDTO;
import com.upemor.app_nutriologo.diario_comidas.dtos.ObservacionRequestDTO;
import com.upemor.app_nutriologo.diario_comidas.entity.DiarioComida;
import com.upemor.app_nutriologo.diario_comidas.mapper.DiarioComidaMapper;
import com.upemor.app_nutriologo.diario_comidas.repository.DiarioComidaRepository;
import com.upemor.app_nutriologo.plan_alimenticio.entity.ComidaProgramada;
import com.upemor.app_nutriologo.plan_alimenticio.repository.ComidaProgramadaRepository;
import com.upemor.app_nutriologo.security.UserContextService;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de gestión del diario de consumo alimenticio.
 * <p>
 * Provee la lógica de negocio para registrar consumos de comida, actualizarlos dentro
 * de la ventana temporal permitida por {@link DiarioVentanaValidator}, consultar historiales
 * y agregar observaciones clínicas por parte del personal de nutrición.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class DiarioComidaServiceImpl implements DiarioComidaService {

    private final DiarioComidaRepository diarioRepository;
    private final ComidaProgramadaRepository comidaProgramadaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DiarioComidaMapper mapper;
    private final UserContextService userContextService;
    private final DiarioVentanaValidator ventanaValidator;

    /**
     * Registra el consumo de una comida para el paciente autenticado.
     * <p>
     * Valida que la fecha se encuentre dentro de la ventana de registro permitida y que no exista
     * un registro previo para el mismo horario en esa fecha.
     * </p>
     *
     * @param dto DTO con los detalles del consumo de comida.
     * @return DTO con la información de la comida registrada.
     * @throws DuplicateResourceException Si ya existe un registro para ese horario y fecha.
     * @throws ResourceNotFoundException Si la comida programada asociada no existe.
     */
    @Override
    @Transactional
    public DiarioComidaResponseDTO registrarComida(DiarioComidaRequestDTO dto) {
        Long pacienteId = userContextService.getCurrentUserId();
        ventanaValidator.validar(dto.fecha());

        diarioRepository.findByPacienteIdAndFechaAndHorario(pacienteId, dto.fecha(), dto.horario())
                .ifPresent(_ -> {
                    throw new DuplicateResourceException("Ya existe un registro para " + dto.horario() + " en esta fecha.");
                });

        DiarioComida nuevo = mapper.toEntity(dto);
        nuevo.setPaciente(usuarioRepository.getReferenceById(pacienteId));

        if (dto.comidaProgramadaId() != null) {
            ComidaProgramada cp = comidaProgramadaRepository.findById(dto.comidaProgramadaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comida programada no encontrada."));
            nuevo.setComidaProgramada(cp);
        }

        return mapper.toResponseDto(diarioRepository.save(nuevo));
    }

    /**
     * Actualiza un registro existente de comida validando permisos y ventana de tiempo.
     *
     * @param id  Identificador único del registro a modificar.
     * @param dto DTO con los nuevos datos del consumo.
     * @return DTO con la información actualizada del registro.
     * @throws ResourceNotFoundException Si el registro no existe.
     * @throws AccessDeniedException Si el usuario autenticado no es el dueño del registro.
     */
    @Override
    @Transactional
    public DiarioComidaResponseDTO actualizarComida(Long id, DiarioComidaRequestDTO dto) {
        Long pacienteId = userContextService.getCurrentUserId();
        DiarioComida registro = diarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));

        if (!registro.getPaciente().getId().equals(pacienteId)) {
            throw new AccessDeniedException("No tienes permiso para editar este registro.");
        }

        ventanaValidator.validar(registro.getFecha());

        registro.setEstado(dto.estado());
        registro.setDetallesComidaReal(dto.detallesComidaReal());
        registro.setHorario(dto.horario());

        return mapper.toResponseDto(diarioRepository.save(registro));
    }

    /**
     * Obtiene el historial de registros de diario de comida de un paciente ordenado por fecha descendente.
     *
     * @param pacienteId Identificador único del paciente.
     * @return Lista de DTOs con el historial de comidas.
     */
    @Override
    @Transactional(readOnly = true)
    public List<DiarioComidaResponseDTO> obtenerHistorialPorPaciente(Long pacienteId) {
        return mapper.toResponseDtoList(diarioRepository.findByPacienteIdOrderByFechaDesc(pacienteId));
    }

    /**
     * Agrega una observación o recomendación clínica del nutriólogo a un registro de comida.
     *
     * @param id  Identificador único del registro al que se agregará la nota.
     * @param dto DTO con el texto de la observación.
     * @return DTO con el registro actualizado.
     * @throws ResourceNotFoundException Si el registro de comida no existe.
     */
    @Override
    @Transactional
    public DiarioComidaResponseDTO agregarObservacion(Long id, ObservacionRequestDTO dto) {
        DiarioComida registro = diarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));

        registro.setObservacionNutriologo(dto.observacion());
        return mapper.toResponseDto(diarioRepository.save(registro));
    }

    /**
     * Elimina permanentemente un registro de diario de comida.
     *
     * @param id Identificador único del registro a eliminar.
     * @throws ResourceNotFoundException Si el registro de comida no existe.
     */
    @Override
    @Transactional
    public void eliminarRegistro(Long id) {
        if (!diarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("El registro no existe.");
        }
        diarioRepository.deleteById(id);
    }
}