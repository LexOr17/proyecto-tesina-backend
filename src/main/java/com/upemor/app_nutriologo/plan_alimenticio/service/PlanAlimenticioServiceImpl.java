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
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanAlimenticioServiceImpl {

    private final PlanAlimenticioRepository planRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlantillaRepository plantillaRepository;
    private final PlanAlimenticioMapper mapper;

    @Transactional
    public PlanAlimenticioResponseDTO crearPlan(PlanAlimenticioRequestDTO dto) {
        // Validar que no exista un plan activo para el paciente
        long planesActivos = planRepository.existsPlanActivoByPacienteId(dto.getPacienteId(), LocalDate.now());
        if (planesActivos > 0) {
            throw new InvalidOperationException("El paciente ya cuenta con un plan alimenticio activo que aún no ha concluido.");
        }

        Usuario paciente = usuarioRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
        Usuario nutriologo = usuarioRepository.findById(dto.getNutriologoId())
                .orElseThrow(() -> new ResourceNotFoundException("Nutriólogo no encontrado"));

        PlanAlimenticio nuevoPlan = mapper.toEntity(dto);
        nuevoPlan.setPaciente(paciente);
        nuevoPlan.setNutriologo(nutriologo);

        procesarComidas(dto.getComidas(), nuevoPlan);

        PlanAlimenticio planGuardado = planRepository.save(nuevoPlan);
        return mapper.toResponseDto(planGuardado);
    }

    @Transactional(readOnly = true)
    public PlanAlimenticioResponseDTO obtenerPlanPorId(Long id) {
        PlanAlimenticio plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));
        return mapper.toResponseDto(plan);
    }

    @Transactional(readOnly = true)
    public List<PlanAlimenticioResponseDTO> obtenerPlanesDePaciente(Long pacienteId) {
        return planRepository.findByPacienteId(pacienteId).stream()
                .map(mapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlanAlimenticioResponseDTO actualizarPlan(Long id, PlanAlimenticioRequestDTO dto) {

        PlanAlimenticio planExistente = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado con ID: " + id));

        mapper.updateEntityFromDto(dto, planExistente);

        planExistente.getComidasProgramadas().clear();
        procesarComidas(dto.getComidas(), planExistente);

        PlanAlimenticio planGuardado = planRepository.save(planExistente);
        return mapper.toResponseDto(planGuardado);
    }

    @Transactional
    public void eliminarPlan(Long id) {
        if (!planRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plan no encontrado");
        }
        planRepository.deleteById(id);
    }

    private void procesarComidas(List<ComidaProgramadaDTO> comidasDto, PlanAlimenticio plan) {
        for (ComidaProgramadaDTO comidaDto : comidasDto) {
            Plantilla plantilla = plantillaRepository.findById(comidaDto.getPlantillaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Plantilla no encontrada con ID: " + comidaDto.getPlantillaId()));

            ComidaProgramada comida = mapper.toEntity(comidaDto);
            comida.setPlantilla(plantilla);

            plan.addComidaProgramada(comida);
        }
    }
}