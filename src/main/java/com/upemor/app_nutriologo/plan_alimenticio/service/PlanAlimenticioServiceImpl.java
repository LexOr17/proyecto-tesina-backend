package com.upemor.app_nutriologo.plan_alimenticio.service;

import com.upemor.app_nutriologo.common.exceptions.InvalidOperationException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.notificaciones.enums.TipoNotificacion;
import com.upemor.app_nutriologo.notificaciones.service.NotificacionService;
import com.upemor.app_nutriologo.plan_alimenticio.dto.ComidaProgramadaDTO;
import com.upemor.app_nutriologo.plan_alimenticio.dto.PlanAlimenticioRequestDTO;
import com.upemor.app_nutriologo.plan_alimenticio.dto.PlanAlimenticioResponseDTO;
import com.upemor.app_nutriologo.plan_alimenticio.entity.ComidaProgramada;
import com.upemor.app_nutriologo.plan_alimenticio.entity.PlanAlimenticio;
import com.upemor.app_nutriologo.plan_alimenticio.mapper.PlanAlimenticioMapper;
import com.upemor.app_nutriologo.plan_alimenticio.repository.PlanAlimenticioRepository;
import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import com.upemor.app_nutriologo.plantillas.repository.PlantillaRepository;
import com.upemor.app_nutriologo.security.UserContextService;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de planes alimenticios.
 * <p>
 * Proporciona la lógica de negocio para la creación, consulta, actualización
 * y eliminación de planes alimenticios, validando la existencia de planes activos,
 * la pertenencia de los registros al nutriólogo autenticado y disparando las
 * notificaciones pertinentes al paciente.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class PlanAlimenticioServiceImpl implements PlanAlimenticioService {

    private final PlanAlimenticioRepository planRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlantillaRepository plantillaRepository;
    private final PlanAlimenticioMapper mapper;
    private final UserContextService userContextService;
    private final NotificacionService notificacionService;

    /**
     * Crea y asigna un nuevo plan alimenticio a un paciente determinado.
     * <p>
     * Valida que el paciente no posea otro plan activo vigente, asocia las comidas
     * programadas con sus plantillas correspondientes y emite una notificación al paciente.
     * </p>
     *
     * @param dto Datos requeridos para la creación del plan alimenticio y sus comidas programadas.
     * @return DTO con los detalles del plan alimenticio creado.
     * @throws InvalidOperationException Si el paciente ya cuenta con un plan alimenticio activo.
     * @throws ResourceNotFoundException Si el paciente o alguna de las plantillas indicadas no existe.
     */
    @Override
    @Transactional
    public PlanAlimenticioResponseDTO crearPlan(PlanAlimenticioRequestDTO dto) {
        Long nutriologoId = userContextService.getCurrentUserId();

        if (planRepository.existsPlanActivo(dto.pacienteId(), LocalDate.now())) {
            throw new InvalidOperationException("El paciente ya cuenta con un plan activo.");
        }

        Usuario paciente = usuarioRepository.findById(dto.pacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
        Usuario nutriologo = usuarioRepository.getReferenceById(nutriologoId);

        PlanAlimenticio nuevoPlan = mapper.toEntity(dto);
        nuevoPlan.setPaciente(paciente);
        nuevoPlan.setNutriologo(nutriologo);

        procesarComidas(dto.comidas(), nuevoPlan);

        PlanAlimenticio planGuardado = planRepository.save(nuevoPlan);

        // Notificar al paciente que se le ha asignado un nuevo plan
        notificacionService.enviarAUsuario(
                dto.pacienteId(),
                TipoNotificacion.PLAN_ASIGNADO,
                "Plan alimenticio asignado",
                "Tu nutriólogo te ha asignado un nuevo plan alimenticio.",
                "/planes/" + planGuardado.getId()
        );

        return mapper.toResponseDto(planGuardado);
    }

    /**
     * Obtiene los detalles de un plan alimenticio a partir de su identificador único.
     *
     * @param id Identificador único del plan alimenticio a consultar.
     * @return DTO con la información detallada del plan alimenticio.
     * @throws ResourceNotFoundException Si el plan alimenticio no existe en el sistema.
     */
    @Override
    @Transactional(readOnly = true)
    public PlanAlimenticioResponseDTO obtenerPlanPorId(Long id) {
        PlanAlimenticio plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));
        return mapper.toResponseDto(plan);
    }

    /**
     * Obtiene todos los planes alimenticios asociados a un paciente específico.
     *
     * @param pacienteId Identificador único del paciente cuyos planes se consultan.
     * @return Lista de DTOs con los planes alimenticios del paciente.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlanAlimenticioResponseDTO> obtenerPlanesDePaciente(Long pacienteId) {
        return planRepository.findByPacienteId(pacienteId).stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    /**
     * Actualiza la información y comidas programadas de un plan alimenticio existente.
     * <p>
     * Verifica que el nutriólogo autenticado sea el creador original del plan
     * antes de aplicar las modificaciones solicitadas.
     * </p>
     *
     * @param id Identificador único del plan alimenticio a actualizar.
     * @param dto Nuevos datos y comidas programadas para el plan.
     * @return DTO con los detalles actualizados del plan alimenticio.
     * @throws ResourceNotFoundException Si el plan o alguna de las plantillas referenciadas no existe.
     * @throws AccessDeniedException Si el usuario autenticado no tiene permisos sobre el plan.
     */
    @Override
    @Transactional
    public PlanAlimenticioResponseDTO actualizarPlan(Long id, PlanAlimenticioRequestDTO dto) {
        Long nutriologoId = userContextService.getCurrentUserId();
        PlanAlimenticio planExistente = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        if (!planExistente.getNutriologo().getId().equals(nutriologoId)) {
            throw new AccessDeniedException("No tienes permiso para modificar este plan alimenticio.");
        }

        mapper.updateEntityFromDto(dto, planExistente);

        // Limpieza segura de la relación bidireccional
        planExistente.getComidasProgramadas().clear();

        procesarComidas(dto.comidas(), planExistente);

        return mapper.toResponseDto(planRepository.save(planExistente));
    }

    /**
     * Elimina un plan alimenticio del sistema tras validar los permisos del usuario.
     *
     * @param id Identificador único del plan alimenticio a eliminar.
     * @throws ResourceNotFoundException Si el plan alimenticio no existe en el sistema.
     * @throws AccessDeniedException Si el usuario autenticado no es el nutriólogo asignado al plan.
     */
    @Override
    @Transactional
    public void eliminarPlan(Long id) {
        Long nutriologoId = userContextService.getCurrentUserId();
        PlanAlimenticio plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        if (!plan.getNutriologo().getId().equals(nutriologoId)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este plan alimenticio.");
        }

        planRepository.delete(plan);
    }

    /**
     * Procesa y asocia en lote las comidas programadas con sus respectivas plantillas al plan alimenticio.
     *
     * @param comidasDto Lista de DTOs con la información de las comidas programadas.
     * @param plan Instancia del plan alimenticio al que se vincularán las comidas.
     * @throws ResourceNotFoundException Si alguna plantilla referenciada no existe en la base de datos.
     */
    private void procesarComidas(List<ComidaProgramadaDTO> comidasDto, PlanAlimenticio plan) {
        Set<Long> plantillaIds = comidasDto.stream()
                .map(comidaDto -> comidaDto.plantillaId())
                .collect(Collectors.toSet());

        Map<Long, Plantilla> plantillasMap = plantillaRepository.findAllById(plantillaIds).stream()
                .collect(Collectors.toMap(plantilla -> plantilla.getId(), plantilla -> plantilla));

        for (ComidaProgramadaDTO dto : comidasDto) {
            Plantilla plantilla = plantillasMap.get(dto.plantillaId());
            if (plantilla == null) {
                throw new ResourceNotFoundException("Plantilla no encontrada: " + dto.plantillaId());
            }
            ComidaProgramada comida = mapper.toEntity(dto);
            comida.setPlantilla(plantilla);
            plan.addComidaProgramada(comida);
        }
    }
}