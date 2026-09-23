package com.upemor.app_nutriologo.diario_extendido.service;

import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoNutriologoRequestDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoPacienteRequestDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface DiarioExtendidoService {

    // El paciente registra su día (hoy)
    DiarioExtendidoResponseDTO registrarOActualizarDiario(DiarioExtendidoPacienteRequestDTO dto);

    // El nutriólogo sugiere (hoy o fechas futuras)
    // Eliminamos 'agregarObservacionesNutriologo' porque este método ya cubre ambos casos
    DiarioExtendidoResponseDTO sugerirActividad(Long pacienteId, LocalDate fecha, DiarioExtendidoNutriologoRequestDTO dto);

    // Consulta por fecha (usada por paciente para su hoy, o nutriólogo para revisar)
    DiarioExtendidoResponseDTO buscarDiarioPorFecha(Long pacienteId, LocalDate fecha);

    // Historial paginado
    Page<DiarioExtendidoResponseDTO> listarDiariosDePaciente(Long pacienteId, Pageable pageable);

    // Solo para el Administrador
    void eliminarDiario(Long diarioId);
}
