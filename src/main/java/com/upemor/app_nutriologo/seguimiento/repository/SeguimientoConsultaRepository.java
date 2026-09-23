package com.upemor.app_nutriologo.seguimiento.repository;

import com.upemor.app_nutriologo.seguimiento.entity.SeguimientoConsulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SeguimientoConsultaRepository extends JpaRepository<SeguimientoConsulta, Long> {

    @EntityGraph(attributePaths = { "paciente", "nutriologo" })
    Page<SeguimientoConsulta> findByPacienteIdOrderByFechaConsultaDesc(Long pacienteId, Pageable pageable);

    @EntityGraph(attributePaths = { "paciente", "nutriologo" })
    Page<SeguimientoConsulta> findAllByOrderByFechaConsultaDesc(Pageable pageable);

    @EntityGraph(attributePaths = { "paciente", "nutriologo" })
    Optional<SeguimientoConsulta> findById(Long id);

    boolean existsByPacienteIdAndFechaInicioAndFechaFin(Long pacienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
