package com.upemor.app_nutriologo.diario_extendido.service;

import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoNutriologoRequestDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoPacienteRequestDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface DiarioExtendidoService {

    // Agregamos pacienteId para saber a quién le estamos guardando el diario
    DiarioExtendidoResponseDTO registrarOActualizarDiario(DiarioExtendidoPacienteRequestDTO dto);

    DiarioExtendidoResponseDTO agregarObservacionesNutriologo(Integer diarioId, DiarioExtendidoNutriologoRequestDTO dto);

    // Agregamos pacienteId porque la combinación Paciente + Fecha es lo que hace único al diario
    DiarioExtendidoResponseDTO buscarDiarioPorFecha(LocalDate fecha);

    Page<DiarioExtendidoResponseDTO> listarDiariosDePaciente(Long pacienteId, Pageable pageable);

    void eliminarDiario(Integer diarioId);
}
