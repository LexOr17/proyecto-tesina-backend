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

    @Query("SELECT COUNT(p) > 0 FROM PlanAlimenticio p " +
            "WHERE p.paciente.id = :pacienteId " +
            "AND p.fechaInicio <= :fechaActual " +
            "AND FUNCTION('ADDDATE', p.fechaInicio, p.duracionDias) >= :fechaActual")
    boolean existsPlanActivo(@Param("pacienteId") Long pacienteId, @Param("fechaActual") LocalDate fechaActual);

    /**
     * Obtiene el plan alimenticio activo del paciente:
     * fechaInicio <= hoy  Y  fechaInicio + duracionDias >= hoy
     * Si hay más de uno (no debería), se toma el más reciente.
     */
    @Query("SELECT p FROM PlanAlimenticio p " +
            "WHERE p.paciente.id = :pacienteId " +
            "AND p.fechaInicio <= :fechaActual " +
            "AND FUNCTION('ADDDATE', p.fechaInicio, p.duracionDias) >= :fechaActual " +
            "ORDER BY p.fechaInicio DESC")
    List<PlanAlimenticio> findPlanesActivosByPacienteId(
            @Param("pacienteId") Long pacienteId,
            @Param("fechaActual") LocalDate fechaActual);
}