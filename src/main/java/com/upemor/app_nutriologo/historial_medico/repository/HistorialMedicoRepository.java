package com.upemor.app_nutriologo.historial_medico.repository;

import com.upemor.app_nutriologo.historial_medico.entity.HistorialMedico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {

    /** Historiales de un paciente paginados, del más reciente al más antiguo */
    Page<HistorialMedico> findByPacienteIdOrderByFechaRegistroDesc(Long pacienteId, Pageable pageable);

    /** El registro más reciente del paciente */
    Optional<HistorialMedico> findTopByPacienteIdOrderByFechaRegistroDesc(Long pacienteId);
}
