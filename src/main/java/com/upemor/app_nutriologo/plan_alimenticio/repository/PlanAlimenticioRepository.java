package com.upemor.app_nutriologo.plan_alimenticio.repository;

import com.upemor.app_nutriologo.plan_alimenticio.entity.PlanAlimenticio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad {@link PlanAlimenticio}.
 * <p>
 * Proporciona métodos de consulta para recuperar planes por paciente o nutriólogo,
 * comprobar la vigencia de planes activos y consultar el plan en curso en una fecha determinada.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface PlanAlimenticioRepository extends JpaRepository<PlanAlimenticio, Long> {

    /**
     * Recupera el historial completo de planes alimenticios asignados a un paciente.
     *
     * @param pacienteId identificador primario del paciente.
     * @return lista de {@link PlanAlimenticio} vinculados al paciente.
     */
    List<PlanAlimenticio> findByPacienteId(Long pacienteId);

    /**
     * Recupera todos los planes alimenticios prescritos por un nutriólogo específico.
     *
     * @param nutriologoId identificador primario del nutriólogo.
     * @return lista de {@link PlanAlimenticio} elaborados por el profesional.
     */
    List<PlanAlimenticio> findByNutriologoId(Long nutriologoId);

    /**
     * Verifica si un paciente tiene al menos un plan alimenticio vigente para una fecha dada.
     *
     * @param pacienteId identificador primario del paciente.
     * @param fechaActual fecha de referencia para verificar la vigencia.
     * @return {@code true} si existe un plan vigente en la fecha dada, {@code false} en caso contrario.
     */
    @Query("SELECT COUNT(p) > 0 FROM PlanAlimenticio p " +
            "WHERE p.paciente.id = :pacienteId " +
            "AND p.fechaInicio <= :fechaActual " +
            "AND FUNCTION('ADDDATE', p.fechaInicio, p.duracionDias) >= :fechaActual")
    boolean existsPlanActivo(@Param("pacienteId") Long pacienteId, @Param("fechaActual") LocalDate fechaActual);

    /**
     * Obtiene los planes alimenticios activos y vigentes de un paciente en una fecha determinada,
     * ordenados del más reciente al más antiguo.
     *
     * @param pacienteId identificador primario del paciente.
     * @param fechaActual fecha actual de consulta.
     * @return lista de {@link PlanAlimenticio} vigentes en la fecha indicada.
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