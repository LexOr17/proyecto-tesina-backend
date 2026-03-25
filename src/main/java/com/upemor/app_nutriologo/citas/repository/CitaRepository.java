package com.upemor.app_nutriologo.citas.repository;

import com.upemor.app_nutriologo.citas.entity.Cita;
import com.upemor.app_nutriologo.citas.enums.Estado;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    @EntityGraph(attributePaths = {"paciente", "nutriologo"})
    List<Cita> findAll();

    @EntityGraph(attributePaths = {"paciente", "nutriologo"})
    Optional<Cita> findById(Long id);

    @EntityGraph(attributePaths = {"paciente", "nutriologo"})
    List<Cita> findAllByPacienteId(Long pacienteId);

    @EntityGraph(attributePaths = {"paciente", "nutriologo"})
    List<Cita> findAllByNutriologoId(Long nutriologoId);

    // Para ver solo las "Solicitadas" o "Pendientes"
    @EntityGraph(attributePaths = {"paciente", "nutriologo"})
    List<Cita> findAllByEstado(Estado estado);

    // --- EL BUSCADOR DE TRASLAPES ---
    // Verifica si ya existe una cita que se cruce con el horario propuesto
    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE c.nutriologo.id = :nutriologoId " +
            "AND c.estado NOT IN (com.upemor.app_nutriologo.citas.enums.Estado.CANCELADA) " +
            "AND (:inicio < c.fechaHoraFin AND :fin > c.fechaHoraInicio)")
    boolean existeCitaEnEseHorario(
            @Param("nutriologoId") Long nutriologoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}
