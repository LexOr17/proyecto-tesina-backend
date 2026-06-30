package com.upemor.app_nutriologo.diario_comidas.repository;

import com.upemor.app_nutriologo.diario_comidas.entity.DiarioComida;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiarioComidaRepository extends JpaRepository<DiarioComida, Long> {

    // Agregamos un EntityGraph para evitar el problema N+1
    @EntityGraph(attributePaths = { "comidaProgramada", "comidaProgramada.plantilla" })
    List<DiarioComida> findByPacienteIdOrderByFechaDesc(Long pacienteId);

    @EntityGraph(attributePaths = { "comidaProgramada", "comidaProgramada.plantilla" })
    List<DiarioComida> findByPacienteIdAndFechaBetween(Long pacienteId, LocalDate inicio, LocalDate fin);

    Optional<DiarioComida> findByPacienteIdAndFechaAndHorario(Long pacienteId, LocalDate fecha, TipoComida horario);
}