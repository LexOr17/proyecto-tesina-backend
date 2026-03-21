package com.upemor.app_nutriologo.diario_extendido.service;

import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoNutriologoRequestDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoPacienteRequestDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoResponseDTO;
import com.upemor.app_nutriologo.diario_extendido.entity.BebidaConsumida;
import com.upemor.app_nutriologo.diario_extendido.entity.DiarioExtendido;
import com.upemor.app_nutriologo.diario_extendido.mapper.DiarioExtendidoMapper;
import com.upemor.app_nutriologo.diario_extendido.repository.DiarioExtendidoRepository;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository; // Asegúrate de tener este repo
import com.upemor.app_nutriologo.security.UserContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DiarioExtendidoServiceImpl implements DiarioExtendidoService { // Ajusté el nombre a la convención

    private final DiarioExtendidoRepository diarioRepository;
    private final PacienteRepository pacienteRepository; // Inyectamos esto para el Proxy
    private final DiarioExtendidoMapper diarioMapper;
    private final UserContextService userContextService;

    @Override
    @Transactional
    public DiarioExtendidoResponseDTO registrarOActualizarDiario(DiarioExtendidoPacienteRequestDTO dto) {
        Long pacienteId = userContextService.getCurrentUserId();

        DiarioExtendido diario = diarioRepository.findByPacienteIdAndFecha(pacienteId, dto.fecha())
                .orElseGet(() -> {
                    DiarioExtendido nuevoDiario = new DiarioExtendido();
                    // Usamos getReferenceById para obtener un proxy seguro para JPA
                    nuevoDiario.setPaciente(pacienteRepository.getReferenceById(pacienteId));
                    return nuevoDiario;
                });

        // Actualizamos los campos básicos (Asegúrate de que el mapper IGNORE la lista de bebidas aquí)
        diarioMapper.updateEntityFromDto(dto, diario);

        // Actualizamos la colección de forma segura usando los helpers de la entidad
        diario.getBebidas().clear();
        if (dto.bebidas() != null) {
            dto.bebidas().forEach(bebidaDto -> {
                BebidaConsumida nuevaBebida = diarioMapper.toEntity(bebidaDto);
                diario.addBebida(nuevaBebida); // Usa el método helper que creamos en la entidad
            });
        }

        return diarioMapper.toResponseDto(diarioRepository.save(diario));
    }

    @Override
    @Transactional
    public DiarioExtendidoResponseDTO agregarObservacionesNutriologo(Integer diarioId, DiarioExtendidoNutriologoRequestDTO dto) {
        Long nutriologoId = userContextService.getCurrentUserId();

        DiarioExtendido diario = diarioRepository.findById(diarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Diario no encontrado"));

        // Validación segura contra NullPointerExceptions
        if (diario.getPaciente().getNutriologo() == null ||
                !diario.getPaciente().getNutriologo().getId().equals(nutriologoId)) {
            throw new AccessDeniedException("No tienes permiso para comentar en este diario.");
        }

        diario.setActividadFisicaSugerida(dto.actividadFisicaSugerida());
        diario.setObservacionesNutriologo(dto.observacionesNutriologo());

        return diarioMapper.toResponseDto(diarioRepository.save(diario));
    }

    @Override
    @Transactional(readOnly = true)
    public DiarioExtendidoResponseDTO buscarDiarioPorFecha(LocalDate fecha) {
        Long pacienteId = userContextService.getCurrentUserId();

        return diarioRepository.findByPacienteIdAndFecha(pacienteId, fecha)
                .map(diarioMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un diario para esa fecha."));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DiarioExtendidoResponseDTO> listarDiariosDePaciente(Long pacienteId, Pageable pageable) {
        Long usuarioAutenticadoId = userContextService.getCurrentUserId();

        // 1. Verificamos si el que consulta es el propio paciente
        boolean esElPropioPaciente = usuarioAutenticadoId.equals(pacienteId);

        // 2. Si no es el paciente, verificamos si es su nutriólogo
        if (!esElPropioPaciente) {
            var paciente = pacienteRepository.findById(pacienteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

            if (paciente.getNutriologo() == null || !paciente.getNutriologo().getId().equals(usuarioAutenticadoId)) {
                throw new AccessDeniedException("No tienes permisos para ver el historial de este paciente.");
            }
        }

        // Nota: Necesitarás agregar este método en tu DiarioExtendidoRepository:
        // Page<DiarioExtendido> findByPacienteIdOrderByFechaDesc(Long pacienteId, Pageable pageable);
        return diarioRepository.findByPacienteIdOrderByFechaDesc(pacienteId, pageable)
                .map(diarioMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void eliminarDiario(Integer diarioId) {
        Long pacienteId = userContextService.getCurrentUserId();

        DiarioExtendido diario = diarioRepository.findById(diarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Diario no encontrado"));

        // Validamos por seguridad que el diario pertenezca al paciente autenticado
        if (!diario.getPaciente().getId().equals(pacienteId)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este diario.");
        }

        // Al borrar el diario, Hibernate borrará automáticamente las bebidas asociadas
        // gracias al cascade = CascadeType.ALL y orphanRemoval = true que pusiste en la entidad
        diarioRepository.delete(diario);
    }
}