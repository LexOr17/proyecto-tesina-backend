package com.upemor.app_nutriologo.plan_alimenticio.repository;

import com.upemor.app_nutriologo.plan_alimenticio.entity.ComidaProgramada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad {@link ComidaProgramada}.
 * <p>
 * Proporciona métodos derivados para consultar las comidas estructuradas dentro de un plan alimenticio general
 * o segmentadas día por día.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface ComidaProgramadaRepository extends JpaRepository<ComidaProgramada, Long> {

    /**
     * Recupera todas las comidas programadas asociadas a un plan alimenticio específico.
     *
     * @param planId identificador primario del plan alimenticio.
     * @return lista de {@link ComidaProgramada} pertenecientes al plan.
     */
    List<ComidaProgramada> findByPlanAlimenticioId(Long planId);

    /**
     * Recupera las comidas programadas de un plan alimenticio para un día secuencial específico.
     *
     * @param planId identificador primario del plan alimenticio.
     * @param diaPlan número del día a consultar dentro de la vigencia del plan.
     * @return lista de {@link ComidaProgramada} programadas para ese día.
     */
    List<ComidaProgramada> findByPlanAlimenticioIdAndDiaPlan(Long planId, Integer diaPlan);
}
