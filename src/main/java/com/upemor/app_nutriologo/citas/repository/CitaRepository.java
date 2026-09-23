package com.upemor.app_nutriologo.citas.repository;

import com.upemor.app_nutriologo.citas.entity.Cita;
import com.upemor.app_nutriologo.citas.enums.Estado;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<Cita, Long> {

        @EntityGraph(attributePaths = { "paciente", "nutriologo" })
        List<Cita> findAll();

        @EntityGraph(attributePaths = { "paciente", "nutriologo" })
        Optional<Cita> findById(Long id);

        @EntityGraph(attributePaths = { "paciente", "nutriologo" })
        List<Cita> findAllByPacienteId(Long pacienteId);

        @EntityGraph(attributePaths = { "paciente", "nutriologo" })
        List<Cita> findAllByNutriologoId(Long nutriologoId);

        // Para ver solo las "Solicitadas" o "Pendientes"
        @EntityGraph(attributePaths = { "paciente", "nutriologo" })
        List<Cita> findAllByEstado(Estado estado);

        // --- EL BUSCADOR DE TRASLAPES ---
        // Verifica si ya existe una cita que se cruce con el horario propuesto
        @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE c.nutriologo.id = :nutriologoId " +
                        "AND c.estado NOT IN (com.upemor.app_nutriologo.citas.enums.Estado.CANCELADA) " +
                        "AND (:inicio < c.fechaHoraFin AND :fin > c.fechaHoraInicio)")
        boolean existeCitaEnEseHorario(
                        @Param("nutriologoId") Long nutriologoId,
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);

        @Query("SELECT COUNT(c) FROM Cita c WHERE c.fechaHoraInicio >= :inicio AND c.fechaHoraInicio <= :fin")
        long countCitasHoy(
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);

        @Query("SELECT c FROM Cita c WHERE c.paciente.id = :pacienteId " +
               "AND c.fechaHoraInicio >= :inicio AND c.fechaHoraInicio <= :fin")
        List<Cita> findByPacienteIdAndRangoFechas(
                        @Param("pacienteId") Long pacienteId,
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);

        // ─── Para el Calendario ──────────────────────────────────────────────

        @EntityGraph(attributePaths = { "paciente", "nutriologo" })
        @Query("SELECT c FROM Cita c WHERE c.nutriologo.id = :nutriologoId " +
               "AND c.fechaHoraInicio >= :inicio AND c.fechaHoraInicio <= :fin " +
               "ORDER BY c.fechaHoraInicio ASC")
        List<Cita> findCitasByNutriologoIdAndRangoFechas(
                        @Param("nutriologoId") Long nutriologoId,
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);

        @EntityGraph(attributePaths = { "paciente", "nutriologo" })
        @Query("SELECT c FROM Cita c WHERE c.paciente.id = :pacienteId " +
               "AND c.fechaHoraInicio >= :inicio AND c.fechaHoraInicio <= :fin " +
               "ORDER BY c.fechaHoraInicio ASC")
        List<Cita> findCitasByPacienteIdAndRangoFechas(
                        @Param("pacienteId") Long pacienteId,
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);

        // ─── Para el Reporte de Asistencia ───────────────────────────────────

        /**
         * Obtiene todas las citas de un nutriólogo específico dentro de un rango de fechas.
         * Usado cuando el ADMINISTRADOR filtra por nutriólogo o el NUTRIÓLOGO consulta las suyas.
         */
        @EntityGraph(attributePaths = { "paciente", "nutriologo" })
        @Query("SELECT c FROM Cita c WHERE c.nutriologo.id = :nutriologoId " +
               "AND c.fechaHoraInicio >= :inicio AND c.fechaHoraInicio <= :fin " +
               "ORDER BY c.fechaHoraInicio ASC")
        List<Cita> findCitasParaReporteAsistenciaPorNutriologo(
                        @Param("nutriologoId") Long nutriologoId,
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);

        /**
         * Obtiene todas las citas de todos los nutriólogos dentro de un rango de fechas.
         * Usado por el ADMINISTRADOR en la vista global.
         */
        @EntityGraph(attributePaths = { "paciente", "nutriologo" })
        @Query("SELECT c FROM Cita c " +
               "WHERE c.fechaHoraInicio >= :inicio AND c.fechaHoraInicio <= :fin " +
               "ORDER BY c.fechaHoraInicio ASC")
        List<Cita> findCitasParaReporteAsistenciaGlobal(
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);
}
