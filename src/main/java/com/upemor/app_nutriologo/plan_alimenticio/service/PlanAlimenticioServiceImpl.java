package com.upemor.app_nutriologo.plan_alimenticio.service;

import com.upemor.app_nutriologo.common.exceptions.InvalidOperationException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.plan_alimenticio.dtos.ComidaProgramadaDTO;
import com.upemor.app_nutriologo.plan_alimenticio.dtos.PlanAlimenticioRequestDTO;
import com.upemor.app_nutriologo.plan_alimenticio.dtos.PlanAlimenticioResponseDTO;
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
@Service
@RequiredArgsConstructor
public class PlanAlimenticioServiceImpl implements PlanAlimenticioService {

    private final PlanAlimenticioRepository planRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlantillaRepository plantillaRepository;
    private final PlanAlimenticioMapper mapper;
    private final UserContextService userContextService;

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

        return mapper.toResponseDto(planRepository.save(nuevoPlan));
    }

    @Override
    @Transactional(readOnly = true)
    public PlanAlimenticioResponseDTO obtenerPlanPorId(Long id) {
        PlanAlimenticio plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));
        return mapper.toResponseDto(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanAlimenticioResponseDTO> obtenerPlanesDePaciente(Long pacienteId) {
        return planRepository.findByPacienteId(pacienteId).stream()
                .map(mapper::toResponseDto)
                .toList();
    }

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

    private void procesarComidas(List<ComidaProgramadaDTO> comidasDto, PlanAlimenticio plan) {
        Set<Long> plantillaIds = comidasDto.stream()
                .map(ComidaProgramadaDTO::plantillaId)
                .collect(Collectors.toSet());

        Map<Long, Plantilla> plantillasMap = plantillaRepository.findAllById(plantillaIds).stream()
                .collect(Collectors.toMap(Plantilla::getId, p -> p));

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