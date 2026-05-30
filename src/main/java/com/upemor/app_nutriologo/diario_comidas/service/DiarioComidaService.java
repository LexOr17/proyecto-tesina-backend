package com.upemor.app_nutriologo.diario_comidas.service;

import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaRequestDTO;
import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaResponseDTO;
import com.upemor.app_nutriologo.diario_comidas.dtos.ObservacionRequestDTO;

import java.util.List;

/**
 * Contrato de servicios para la gestión del diario alimenticio.
 * Define las operaciones permitidas para Pacientes, Nutriólogos y Administradores.
 */
public interface DiarioComidaService {

    /**
     * Registra un nuevo consumo para el paciente autenticado.
     */
    DiarioComidaResponseDTO registrarComida(DiarioComidaRequestDTO dto);

    /**
     * Actualiza un registro existente (Solo permitido bajo la regla de ventana de tiempo).
     */
    DiarioComidaResponseDTO actualizarComida(Long id, DiarioComidaRequestDTO dto);

    /**
     * Recupera el historial de un paciente específico.
     * @param pacienteId ID del paciente a consultar.
     */
    List<DiarioComidaResponseDTO> obtenerHistorialPorPaciente(Long pacienteId);

    /**
     * Permite al nutriólogo añadir una nota o feedback a un registro de comida.
     */
    DiarioComidaResponseDTO agregarObservacion(Long id, ObservacionRequestDTO dto);

    /**
     * Elimina permanentemente un registro (Operación administrativa).
     */
    void eliminarRegistro(Long id);

    /**
     * (Opcional) Obtiene registros en un rango de fechas para reportes específicos.
     */
//    List<DiarioComidaResponseDTO> obtenerDiarioPorRango(Long pacienteId, LocalDate inicio, LocalDate fin);
}