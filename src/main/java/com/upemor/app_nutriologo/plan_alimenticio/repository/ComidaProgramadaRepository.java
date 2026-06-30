package com.upemor.app_nutriologo.plan_alimenticio.repository;

import com.upemor.app_nutriologo.plan_alimenticio.entity.ComidaProgramada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComidaProgramadaRepository extends JpaRepository<ComidaProgramada, Long> {

    // Trae todas las comidas de un plan específico
    List<ComidaProgramada> findByPlanAlimenticioId(Long planId);

    // Útil si en el futuro quieres cargar el calendario día por día en el frontend
    // para no traer todas las comidas de golpe si el plan es de 3 meses.
    List<ComidaProgramada> findByPlanAlimenticioIdAndDiaPlan(Long planId, Integer diaPlan);
}
