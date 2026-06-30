package com.upemor.app_nutriologo.diario_extendido.repository;

import com.upemor.app_nutriologo.diario_extendido.entity.DiarioExtendido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiarioExtendidoRepository extends JpaRepository<DiarioExtendido, Long> {

    @EntityGraph(attributePaths = { "bebidas" })
    Optional<DiarioExtendido> findByPacienteIdAndFecha(Long pacienteId, LocalDate fecha);

    // NUEVO: Útil para mostrar todo el historial de un paciente
    @EntityGraph(attributePaths = { "bebidas" })
    List<DiarioExtendido> findAllByPacienteIdOrderByFechaDesc(Long pacienteId);

    Page<DiarioExtendido> findByPacienteIdOrderByFechaDesc(Long pacienteId, Pageable pageable);

    // NUEVO: Útil para mostrar los diarios de una semana o un mes específico
    List<DiarioExtendido> findByPacienteIdAndFechaBetweenOrderByFechaAsc(
            Long pacienteId,
            LocalDate fechaInicio,
            LocalDate fechaFin);
}