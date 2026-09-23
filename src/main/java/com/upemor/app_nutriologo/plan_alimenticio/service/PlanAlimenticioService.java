package com.upemor.app_nutriologo.plan_alimenticio.service;

import com.upemor.app_nutriologo.plan_alimenticio.dto.PlanAlimenticioRequestDTO;
import com.upemor.app_nutriologo.plan_alimenticio.dto.PlanAlimenticioResponseDTO;

import java.util.List;

/**
 * Contrato de servicios para la administración y prescripción de planes alimenticios.
 * <p>
 * Define las operaciones para la creación, consulta, actualización y eliminación
 * de planes nutricionales y sus respectivas comidas programadas.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface PlanAlimenticioService {

    /**
     * Crea y programa un nuevo plan alimenticio para un paciente.
     *
     * @param dto datos de la solicitud con las comidas y duración del plan.
     * @return {@link PlanAlimenticioResponseDTO} con los datos del plan registrado.
     */
    PlanAlimenticioResponseDTO crearPlan(PlanAlimenticioRequestDTO dto);

    /**
     * Localiza un plan alimenticio a partir de su identificador primario.
     *
     * @param id identificador primario del plan.
     * @return {@link PlanAlimenticioResponseDTO} del plan encontrado.
     */
    PlanAlimenticioResponseDTO obtenerPlanPorId(Long id);

    /**
     * Recupera todos los planes alimenticios prescritos para un paciente específico.
     *
     * @param pacienteId identificador primario del paciente.
     * @return lista de {@link PlanAlimenticioResponseDTO}.
     */
    List<PlanAlimenticioResponseDTO> obtenerPlanesDePaciente(Long pacienteId);

    /**
     * Actualiza los datos y comidas programadas de un plan alimenticio existente.
     *
     * @param id identificador primario del plan a modificar.
     * @param dto nuevos datos y comidas a actualizar en el plan.
     * @return {@link PlanAlimenticioResponseDTO} con los datos actualizados.
     */
    PlanAlimenticioResponseDTO actualizarPlan(Long id, PlanAlimenticioRequestDTO dto);

    /**
     * Elimina un plan alimenticio del sistema a partir de su identificador primario.
     *
     * @param id identificador primario del plan a eliminar.
     */
    void eliminarPlan(Long id);
}