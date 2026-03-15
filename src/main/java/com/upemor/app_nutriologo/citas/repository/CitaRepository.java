package com.upemor.app_nutriologo.citas.repository;

import com.upemor.app_nutriologo.citas.entity.Cita;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
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

    List<Cita> findByNutriologoIdAndFechaHoraInicioBetween(Long nutriologoId, LocalDateTime start, LocalDateTime end);
}
