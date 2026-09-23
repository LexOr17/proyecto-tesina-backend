package com.upemor.app_nutriologo.sugerencia_plantilla.repository;

import com.upemor.app_nutriologo.sugerencia_plantilla.entity.SugerenciaPlantilla;
import com.upemor.app_nutriologo.sugerencia_plantilla.enums.EstadoSugerencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SugerenciaPlantillaRepository extends JpaRepository<SugerenciaPlantilla, Long> {

    /** Sugerencias enviadas por un paciente concreto */
    Page<SugerenciaPlantilla> findByPacienteId(Long pacienteId, Pageable pageable);

    /** Sugerencias enviadas por un paciente concreto filtradas por estado */
    Page<SugerenciaPlantilla> findByPacienteIdAndEstado(Long pacienteId, EstadoSugerencia estado, Pageable pageable);

    /** Todas las sugerencias recibidas por un nutriólogo */
    Page<SugerenciaPlantilla> findByNutriologoId(Long nutriologoId, Pageable pageable);

    /** Sugerencias filtradas por nutriólogo y estado (ej. PENDIENTE) */
    Page<SugerenciaPlantilla> findByNutriologoIdAndEstado(Long nutriologoId, EstadoSugerencia estado,
            Pageable pageable);

    /**
     * Consulta optimizada con fetch join para el Reporte de Propuestas de Plan por Nutriólogo
     */
    @Query("SELECT DISTINCT s FROM SugerenciaPlantilla s " +
           "LEFT JOIN FETCH s.paciente p " +
           "LEFT JOIN FETCH s.detalles d " +
           "LEFT JOIN FETCH d.alimento a " +
           "WHERE s.nutriologo.id = :nutriologoId " +
           "AND s.fechaEnvio BETWEEN :inicio AND :fin " +
           "ORDER BY s.fechaEnvio DESC")
    List<SugerenciaPlantilla> findSugerenciasParaReportePorNutriologo(
            @Param("nutriologoId") Long nutriologoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Consulta optimizada con fetch join para el Reporte de Propuestas de Plan Global
     */
    @Query("SELECT DISTINCT s FROM SugerenciaPlantilla s " +
           "LEFT JOIN FETCH s.paciente p " +
           "LEFT JOIN FETCH s.detalles d " +
           "LEFT JOIN FETCH d.alimento a " +
           "WHERE s.fechaEnvio BETWEEN :inicio AND :fin " +
           "ORDER BY s.fechaEnvio DESC")
    List<SugerenciaPlantilla> findSugerenciasParaReporteGlobal(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}
