package com.upemor.app_nutriologo.historial_medico.service;

import com.upemor.app_nutriologo.historial_medico.dto.HistorialMedicoRequestDTO;
import com.upemor.app_nutriologo.historial_medico.dto.HistorialMedicoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistorialMedicoService {

    /** NUTRIÓLOGO: registra un nuevo historial médico para un paciente */
    HistorialMedicoResponseDTO registrarHistorial(Long pacienteId, HistorialMedicoRequestDTO requestDTO);

    /** NUTRIÓLOGO / PACIENTE: obtiene los historiales de un paciente paginados (más reciente primero) */
    Page<HistorialMedicoResponseDTO> obtenerHistorialesPorPaciente(Long pacienteId, Pageable pageable);

    /** NUTRIÓLOGO / PACIENTE: obtiene el historial más reciente del paciente */
    HistorialMedicoResponseDTO obtenerUltimoHistorial(Long pacienteId);

    /** NUTRIÓLOGO / PACIENTE: obtiene un historial específico por ID */
    HistorialMedicoResponseDTO obtenerHistorialPorId(Long historialId);

    /** NUTRIÓLOGO: actualiza un historial médico existente */
    HistorialMedicoResponseDTO actualizarHistorial(Long historialId, HistorialMedicoRequestDTO requestDTO);

    /** NUTRIÓLOGO: elimina un historial médico */
    void eliminarHistorial(Long historialId);
}


