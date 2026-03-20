package com.upemor.app_nutriologo.plan_alimenticio.repository;

import com.upemor.app_nutriologo.plan_alimenticio.entity.PlanAlimenticio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlanAlimenticioRepository extends JpaRepository<PlanAlimenticio, Long> {

    List<PlanAlimenticio> findByPacienteId(Long pacienteId);

    List<PlanAlimenticio> findByNutriologoId(Long nutriologoId);

    // Consulta nativa para verificar planes activos, más compatible con MySQL
    @Query(value = "SELECT CASE WHEN COUNT(p.id) > 0 THEN 1 ELSE 0 END FROM planes_alimenticios p " +
           "WHERE p.paciente_id = :pacienteId " +
           "AND DATE_ADD(p.fecha_inicio, INTERVAL p.duracion_dias DAY) >= :fechaActual", 
           nativeQuery = true)
    long existsPlanActivoByPacienteId(@Param("pacienteId") Long pacienteId, @Param("fechaActual") LocalDate fechaActual);
}