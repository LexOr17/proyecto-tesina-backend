package com.upemor.app_nutriologo.citas.service;

import com.upemor.app_nutriologo.citas.dto.CitaCancelacionRequestDTO;
import com.upemor.app_nutriologo.citas.dto.CitaReprogramarDTO;
import com.upemor.app_nutriologo.citas.dto.CitaRequestDTO;
import com.upemor.app_nutriologo.citas.dto.CitaResponseDTO;
import com.upemor.app_nutriologo.citas.dto.CitaSolicitudDTO;
import com.upemor.app_nutriologo.citas.dto.FinalizarCitaDTO;

import java.util.List;

public interface CitaService {
    CitaResponseDTO crearCita(CitaRequestDTO citaRequestDTO);
    List<CitaResponseDTO> obtenerTodasLasCitas();
    CitaResponseDTO obtenerCitaPorId(Long id);
    CitaResponseDTO actualizarCita(Long id, CitaRequestDTO citaRequestDTO);
    void eliminarCita(Long id);
    List<CitaResponseDTO> obtenerCitasPorPacienteId(Long pacienteId);
    List<CitaResponseDTO> obtenerCitasPorNutriologoId(Long nutriologoId);

    // Nuevos métodos
    CitaResponseDTO solicitarCita(Long pacienteId, CitaSolicitudDTO solicitudDTO);
    CitaResponseDTO cancelarCita(Long citaId);
    CitaResponseDTO solicitarCancelacionCita(Long citaId, CitaCancelacionRequestDTO cancelacionRequestDTO);
    CitaResponseDTO reprogramarCita(Long citaId, CitaReprogramarDTO reprogramarDTO);
    CitaResponseDTO finalizarCita(Long citaId, FinalizarCitaDTO finalizarCitaDTO);
}
