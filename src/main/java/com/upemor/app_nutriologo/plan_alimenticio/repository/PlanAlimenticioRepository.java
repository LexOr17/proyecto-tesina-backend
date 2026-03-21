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

    // Versión JPQL: Más limpia y no depende de funciones específicas de MySQL
    @Query("SELECT COUNT(p) > 0 FROM PlanAlimenticio p " +
            "WHERE p.paciente.id = :pacienteId " +
            "AND p.fechaInicio <= :fechaActual " +
            "AND FUNCTION('DATE_ADD', p.fechaInicio, p.duracionDias, 'DAY') >= :fechaActual")
    boolean existsPlanActivo(@Param("pacienteId") Long pacienteId, @Param("fechaActual") LocalDate fechaActual);
}