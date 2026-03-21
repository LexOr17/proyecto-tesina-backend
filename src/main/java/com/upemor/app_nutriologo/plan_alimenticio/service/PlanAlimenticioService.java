package com.upemor.app_nutriologo.plan_alimenticio.service;

import com.upemor.app_nutriologo.plan_alimenticio.dtos.PlanAlimenticioRequestDTO;
import com.upemor.app_nutriologo.plan_alimenticio.dtos.PlanAlimenticioResponseDTO;

import java.util.List;

public interface PlanAlimenticioService {

    PlanAlimenticioResponseDTO crearPlan(PlanAlimenticioRequestDTO dto);

    PlanAlimenticioResponseDTO obtenerPlanPorId(Long id);

    List<PlanAlimenticioResponseDTO> obtenerPlanesDePaciente(Long pacienteId);

    PlanAlimenticioResponseDTO actualizarPlan(Long id, PlanAlimenticioRequestDTO dto);

    void eliminarPlan(Long id);
}