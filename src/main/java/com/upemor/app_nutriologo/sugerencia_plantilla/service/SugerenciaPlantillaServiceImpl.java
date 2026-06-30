package com.upemor.app_nutriologo.sugerencia_plantilla.service;

import com.upemor.app_nutriologo.alimento.entity.Alimento;
import com.upemor.app_nutriologo.alimento.repository.AlimentoRepository;
import com.upemor.app_nutriologo.common.exceptions.InvalidOperationException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import com.upemor.app_nutriologo.plan_alimenticio.entity.PlanAlimenticio;
import com.upemor.app_nutriologo.plan_alimenticio.repository.PlanAlimenticioRepository;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaAlimentoRequestDTO;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaResponseDTO;
import com.upemor.app_nutriologo.plantillas.mapper.PlantillaMapper;
import com.upemor.app_nutriologo.plantillas.repository.PlantillaRepository;
import com.upemor.app_nutriologo.security.UserContextService;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaPlantillaRequestDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaPlantillaResponseDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaRevisionDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.entity.SugerenciaPlantilla;
import com.upemor.app_nutriologo.sugerencia_plantilla.enums.EstadoSugerencia;
import com.upemor.app_nutriologo.sugerencia_plantilla.mapper.SugerenciaPlantillaMapper;
import com.upemor.app_nutriologo.sugerencia_plantilla.repository.SugerenciaPlantillaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SugerenciaPlantillaServiceImpl implements SugerenciaPlantillaService {

    private final SugerenciaPlantillaRepository sugerenciaRepository;
    private final PacienteRepository pacienteRepository;
    private final PlantillaRepository plantillaRepository;
    private final PlanAlimenticioRepository planAlimenticioRepository;
    private final AlimentoRepository alimentoRepository;
    private final SugerenciaPlantillaMapper mapper;
    private final PlantillaMapper plantillaMapper;
    private final UserContextService userContextService;

    // =========================================================
    // PACIENTE
    // =========================================================

    @Override
    @Transactional
    public SugerenciaPlantillaResponseDTO enviarSugerencia(SugerenciaPlantillaRequestDTO requestDTO) {
        Long usuarioId = userContextService.getCurrentUserId();

        Paciente paciente = pacienteRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + usuarioId));

        Nutriologo nutriologo = paciente.getNutriologo();

        // Si se indicó una plantilla original, validar que exista y le pertenezca
        if (requestDTO.plantillaOriginalId() != null) {
            boolean esDuenioDirecto = plantillaRepository.findByUsuarioId(usuarioId).stream()
                    .anyMatch(p -> p.getId().equals(requestDTO.plantillaOriginalId()));

            // También aceptar plantillas del plan alimenticio activo del paciente
            boolean esDePlantillaDelPlan = planAlimenticioRepository
                    .findPlanesActivosByPacienteId(usuarioId, LocalDate.now())
                    .stream()
                    .flatMap(plan -> plan.getComidasProgramadas().stream())
                    .map(comida -> comida.getPlantilla())
                    .anyMatch(p -> p.getId().equals(requestDTO.plantillaOriginalId()));

            // También aceptar sugerencias propias previas
            boolean esSugerenciaPropia = sugerenciaRepository
                    .findByPacienteId(usuarioId, Pageable.unpaged())
                    .stream()
                    .anyMatch(s -> s.getId().equals(requestDTO.plantillaOriginalId()));

            if (!esDuenioDirecto && !esDePlantillaDelPlan && !esSugerenciaPropia) {
                throw new AccessDeniedException(
                        "La plantilla original no pertenece al paciente o no forma parte de su plan activo.");
            }
        }

        SugerenciaPlantilla sugerencia = SugerenciaPlantilla.builder()
                .nombre(requestDTO.nombre())
                .tipoComida(requestDTO.tipoComida())
                .motivoSugerencia(requestDTO.motivoSugerencia())
                .plantillaOriginalId(requestDTO.plantillaOriginalId())
                .estado(EstadoSugerencia.PENDIENTE)
                .paciente(paciente)
                .nutriologo(nutriologo)
                .build();

        vincularAlimentos(sugerencia, requestDTO.alimentos());

        return mapper.toDto(sugerenciaRepository.save(sugerencia));
    }

    @Override
    @Transactional
    public SugerenciaPlantillaResponseDTO actualizarSugerencia(Long id, SugerenciaPlantillaRequestDTO requestDTO) {
        Long pacienteId = userContextService.getCurrentUserId();

        SugerenciaPlantilla sugerencia = obtenerYValidarSugerencia(id);

        // Solo el paciente dueño puede editar
        if (!sugerencia.getPaciente().getId().equals(pacienteId)) {
            throw new AccessDeniedException("No tienes permiso para editar esta sugerencia.");
        }

        // Solo se puede editar si está PENDIENTE
        if (sugerencia.getEstado() != EstadoSugerencia.PENDIENTE) {
            throw new InvalidOperationException(
                    "No se puede editar una sugerencia que ya fue revisada (estado: " + sugerencia.getEstado() + ").");
        }

        // Actualizar campos básicos
        sugerencia.setNombre(requestDTO.nombre());
        sugerencia.setTipoComida(requestDTO.tipoComida());
        sugerencia.setMotivoSugerencia(requestDTO.motivoSugerencia());
        sugerencia.setPlantillaOriginalId(requestDTO.plantillaOriginalId());

        // Reemplazar completamente la lista de alimentos
        sugerencia.getDetalles().clear();
        vincularAlimentos(sugerencia, requestDTO.alimentos());

        return mapper.toDto(sugerenciaRepository.save(sugerencia));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlantillaResponseDTO> obtenerPlantillasDelPlanActual() {
        Long pacienteId = userContextService.getCurrentUserId();

        // Verificar que el paciente existe
        pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + pacienteId));

        // Buscar el plan activo
        List<PlanAlimenticio> planesActivos = planAlimenticioRepository
                .findPlanesActivosByPacienteId(pacienteId, LocalDate.now());

        if (planesActivos.isEmpty()) {
            throw new ResourceNotFoundException("No tienes un plan alimenticio activo en este momento.");
        }

        // Tomar el plan más reciente (primero de la lista, ya ordenada por fechaInicio
        // DESC)
        PlanAlimenticio planActivo = planesActivos.getFirst();

        // Extraer las plantillas únicas de las comidas programadas del plan
        return planActivo.getComidasProgramadas().stream()
                .map(comida -> comida.getPlantilla())
                .distinct()
                .map(plantillaMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SugerenciaPlantillaResponseDTO> obtenerSugerenciasDePaciente(Pageable pageable) {
        Long pacienteId = userContextService.getCurrentUserId();
        return sugerenciaRepository.findByPacienteId(pacienteId, pageable)
                .map(mapper::toDto);
    }

    // =========================================================
    // NUTRIÓLOGO
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<SugerenciaPlantillaResponseDTO> obtenerSugerenciasParaNutriologo(Pageable pageable) {
        Long nutriologoId = userContextService.getCurrentUserId();
        return sugerenciaRepository.findByNutriologoId(nutriologoId, pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SugerenciaPlantillaResponseDTO> obtenerPendientesParaNutriologo(Pageable pageable) {
        Long nutriologoId = userContextService.getCurrentUserId();
        return sugerenciaRepository.findByNutriologoIdAndEstado(nutriologoId, EstadoSugerencia.PENDIENTE, pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public SugerenciaPlantillaResponseDTO revisarSugerencia(Long id, SugerenciaRevisionDTO revisionDTO) {
        // El estado debe ser APROBADA o RECHAZADA
        if (revisionDTO.estado() == EstadoSugerencia.PENDIENTE) {
            throw new InvalidOperationException("El estado de revisión debe ser APROBADA o RECHAZADA.");
        }

        Long nutriologoId = userContextService.getCurrentUserId();
        SugerenciaPlantilla sugerencia = obtenerYValidarSugerencia(id);

        // Validar que la sugerencia está dirigida a este nutriólogo
        if (!sugerencia.getNutriologo().getId().equals(nutriologoId)) {
            throw new AccessDeniedException("No tienes permiso para revisar esta sugerencia.");
        }

        // Solo se pueden revisar sugerencias PENDIENTES
        if (sugerencia.getEstado() != EstadoSugerencia.PENDIENTE) {
            throw new InvalidOperationException(
                    "Esta sugerencia ya fue revisada con estado: " + sugerencia.getEstado());
        }

        sugerencia.setEstado(revisionDTO.estado());
        sugerencia.setComentarioNutriologo(revisionDTO.comentarioNutriologo());
        sugerencia.setFechaRevision(LocalDateTime.now());

        return mapper.toDto(sugerenciaRepository.save(sugerencia));
    }

    // =========================================================
    // COMPARTIDO
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public SugerenciaPlantillaResponseDTO obtenerSugerenciaPorId(Long id) {
        SugerenciaPlantilla sugerencia = obtenerYValidarSugerencia(id);
        Long usuarioId = userContextService.getCurrentUserId();

        boolean esPacienteDuenio = sugerencia.getPaciente().getId().equals(usuarioId);
        boolean esNutriologoDuenio = sugerencia.getNutriologo().getId().equals(usuarioId);

        if (!esPacienteDuenio && !esNutriologoDuenio) {
            throw new AccessDeniedException("No tienes acceso a esta sugerencia.");
        }

        return mapper.toDto(sugerencia);
    }

    @Override
    @Transactional
    public void eliminarSugerencia(Long id) {
        Long pacienteId = userContextService.getCurrentUserId();
        SugerenciaPlantilla sugerencia = obtenerYValidarSugerencia(id);

        if (!sugerencia.getPaciente().getId().equals(pacienteId)) {
            throw new AccessDeniedException("No tienes permiso para eliminar esta sugerencia.");
        }

        if (sugerencia.getEstado() != EstadoSugerencia.PENDIENTE) {
            throw new InvalidOperationException(
                    "No se puede eliminar una sugerencia que ya fue revisada (estado: " + sugerencia.getEstado()
                            + ").");
        }

        sugerenciaRepository.delete(sugerencia);
    }

    // =========================================================
    // Métodos privados de apoyo
    // =========================================================

    private SugerenciaPlantilla obtenerYValidarSugerencia(Long id) {
        return sugerenciaRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Sugerencia de plantilla no encontrada con id: " + id));
    }

    private void vincularAlimentos(SugerenciaPlantilla sugerencia, List<PlantillaAlimentoRequestDTO> alimentoDtos) {
        Set<Long> ids = alimentoDtos.stream()
                .map(dto -> dto.alimentoId())
                .collect(Collectors.toSet());

        Map<Long, Alimento> alimentosMap = alimentoRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(dto -> dto.getId(), a -> a));

        if (alimentosMap.size() != ids.size()) {
            throw new ResourceNotFoundException("Uno o más alimentos no existen en el catálogo.");
        }

        for (PlantillaAlimentoRequestDTO dto : alimentoDtos) {
            sugerencia.addAlimento(alimentosMap.get(dto.alimentoId()), dto.cantidad());
        }
    }
}