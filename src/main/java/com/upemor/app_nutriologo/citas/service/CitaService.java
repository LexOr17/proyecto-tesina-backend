package com.upemor.app_nutriologo.citas.service;

import com.upemor.app_nutriologo.citas.dto.CitaEstadoUpdateDTO;
import com.upemor.app_nutriologo.citas.dto.CitaRequestDTO;
import com.upemor.app_nutriologo.citas.dto.CitaResponseDTO;
import com.upemor.app_nutriologo.citas.dto.CitaSolicitudDTO;

import java.util.List;

public interface CitaService {
    // ... tus métodos anteriores ...
    CitaResponseDTO crearCita(CitaRequestDTO citaRequestDTO);

    // Método para manejar la solicitud específica que causaba el error
    CitaResponseDTO solicitarCita(Long pacienteId, CitaSolicitudDTO solicitudDTO);

    List<CitaResponseDTO> obtenerTodasLasCitas();
    CitaResponseDTO obtenerCitaPorId(Long id);
    CitaResponseDTO actualizarCita(Long id, CitaRequestDTO citaRequestDTO);
    void eliminarCita(Long id);
    List<CitaResponseDTO> obtenerCitasPorPacienteId(Long pacienteId);
    List<CitaResponseDTO> obtenerCitasPorNutriologoId(Long nutriologoId);
    CitaResponseDTO actualizarEstadoCita(Long citaId, CitaEstadoUpdateDTO estadoUpdateDTO);
}
