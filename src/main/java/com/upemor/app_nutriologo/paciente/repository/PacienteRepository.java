package com.upemor.app_nutriologo.paciente.repository;

import com.upemor.app_nutriologo.paciente.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio de acceso a datos para la entidad {@link Paciente}.
 * <p>
 * Proporciona métodos de consulta personalizados para la gestión de pacientes por nutriólogo,
 * paginación de expedientes y cálculo de indicadores demográficos y de actividad.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    /**
     * Recupera de manera paginada todos los pacientes asignados a un nutriólogo específico.
     *
     * @param nutriologoId identificador primario del nutriólogo.
     * @param pageable configuración de paginación y ordenamiento.
     * @return página {@link Page} de {@link Paciente} bajo la atención del nutriólogo.
     */
    @Query("SELECT p FROM Paciente p WHERE p.nutriologo.id = :nutriologoId")
    Page<Paciente> findByNutriologoId(@Param("nutriologoId") Long nutriologoId, Pageable pageable);

    /**
     * Contabiliza el número total de pacientes activos que se encuentran bajo la tutela de un nutriólogo.
     *
     * @param nutriologoId identificador primario del nutriólogo.
     * @return cantidad de pacientes con estado activo asignados al profesional.
     */
    @Query("SELECT COUNT(p) FROM Paciente p WHERE p.nutriologo.id = :nutriologoId AND (p.activo IS NULL OR p.activo = true)")
    long countPacientesActivosByNutriologoId(@Param("nutriologoId") Long nutriologoId);

    /**
     * Contabiliza el número global de pacientes activos registrados en el sistema.
     *
     * @return total de pacientes activos en la plataforma.
     */
    @Query("SELECT COUNT(p) FROM Paciente p WHERE (p.activo IS NULL OR p.activo = true)")
    long countPacientesActivosTotal();
}
