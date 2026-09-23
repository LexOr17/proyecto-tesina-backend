package com.upemor.app_nutriologo.sugerencia_plantilla.service;

import com.upemor.app_nutriologo.alimento.entity.Alimento;
import com.upemor.app_nutriologo.alimento.repository.AlimentoRepository;
import com.upemor.app_nutriologo.common.exceptions.InvalidOperationException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.notificaciones.enums.TipoNotificacion;
import com.upemor.app_nutriologo.notificaciones.service.NotificacionService;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import com.upemor.app_nutriologo.plan_alimenticio.entity.PlanAlimenticio;
import com.upemor.app_nutriologo.plan_alimenticio.repository.PlanAlimenticioRepository;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaAlimentoRequestDTO;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaResponseDTO;
import com.upemor.app_nutriologo.plantillas.mapper.PlantillaMapper;
import com.upemor.app_nutriologo.security.UserContextService;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaPlantillaRequestDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaPlantillaResponseDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaRevisionDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.entity.SugerenciaPlantilla;
import com.upemor.app_nutriologo.sugerencia_plantilla.enums.CategoriaMotivoSugerencia;
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
    private final AlimentoRepository alimentoRepository;
    private final PlanAlimenticioRepository planAlimenticioRepository;
    private final SugerenciaPlantillaMapper mapper;
    private final PlantillaMapper plantillaMapper;
    private final UserContextService userContextService;
    private final NotificacionService notificacionService;

    // =========================================================
    // PACIENTE
    // =========================================================

    @Override
    @Transactional
    public SugerenciaPlantillaResponseDTO enviarSugerencia(SugerenciaPlantillaRequestDTO requestDTO) {
        Long pacienteId = userContextService.getCurrentUserId();

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + pacienteId));

        Nutriologo nutriologo = paciente.getNutriologo();
        if (nutriologo == null) {
            throw new InvalidOperationException("El paciente no tiene un nutriólogo asignado.");
        }

        CategoriaMotivoSugerencia categoria = requestDTO.categoriaMotivo() != null
                ? requestDTO.categoriaMotivo()
                : CategoriaMotivoSugerencia.PREFERENCIA_SABOR;

        SugerenciaPlantilla sugerencia = SugerenciaPlantilla.builder()
                .nombre(requestDTO.nombre())
                .tipoComida(requestDTO.tipoComida())
                .categoriaMotivo(categoria)
                .motivoSugerencia(requestDTO.motivoSugerencia())
                .plantillaOriginalId(requestDTO.plantillaOriginalId())
                .estado(EstadoSugerencia.PENDIENTE)
                .paciente(paciente)
                .nutriologo(nutriologo)
                .build();

        vincularAlimentos(sugerencia, requestDTO.alimentos());

        SugerenciaPlantilla guardada = sugerenciaRepository.save(sugerencia);

        // Notificar al nutriólogo sobre la nueva sugerencia recibida
        String pacienteNombre = paciente.getNombre() + " " + (paciente.getApellidoPaterno() != null ? paciente.getApellidoPaterno() : "");
        notificacionService.enviarAUsuario(
                nutriologo.getId(),
                TipoNotificacion.NUEVA_SUGERENCIA,
                "Nueva sugerencia de plantilla",
                pacienteNombre.trim() + " envió una sugerencia: " + guardada.getNombre() + ".",
                "/main/sugerencias"
        );

        return mapper.toDto(guardada);
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
        if (requestDTO.categoriaMotivo() != null) {
            sugerencia.setCategoriaMotivo(requestDTO.categoriaMotivo());
        }
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

        // Tomar el plan más reciente (primero de la lista, ya ordenada por fechaInicio DESC)
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
    public Page<SugerenciaPlantillaResponseDTO> obtenerSugerenciasDePaciente(Pageable pageable, EstadoSugerencia estado) {
        Long pacienteId = userContextService.getCurrentUserId();
        Page<SugerenciaPlantilla> page = (estado != null)
                ? sugerenciaRepository.findByPacienteIdAndEstado(pacienteId, estado, pageable)
                : sugerenciaRepository.findByPacienteId(pacienteId, pageable);
        return page.map(mapper::toDto);
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

        SugerenciaPlantilla guardada = sugerenciaRepository.save(sugerencia);

        // Notificar al paciente sobre la revisión de su sugerencia
        String estadoTexto = guardada.getEstado() == EstadoSugerencia.APROBADA ? "aprobada" : "rechazada";
        String comentario = revisionDTO.comentarioNutriologo() != null && !revisionDTO.comentarioNutriologo().isBlank()
                ? " Comentario: " + revisionDTO.comentarioNutriologo()
                : "";

        notificacionService.enviarAUsuario(
                guardada.getPaciente().getId(),
                TipoNotificacion.SUGERENCIA_REVISADA,
                "Sugerencia " + estadoTexto,
                "Tu sugerencia \"" + guardada.getNombre() + "\" ha sido " + estadoTexto + "." + comentario,
                "/main/mis-sugerencias"
        );

        return mapper.toDto(guardada);
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
