package com.upemor.app_nutriologo.diario_comidas.service;

import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaRequestDTO;
import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaResponseDTO;
import com.upemor.app_nutriologo.diario_comidas.dtos.ObservacionRequestDTO;

import java.util.List;

/**
 * Contrato de servicios para la gestión del diario de consumo alimenticio.
 * <p>
 * Define las operaciones permitidas para que los pacientes registren sus comidas diarias
 * y los nutriólogos revisen historiales y agreguen retroalimentación clínica.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface DiarioComidaService {

    /**
     * Registra un nuevo consumo de comida para el paciente autenticado.
     *
     * @param dto Datos del registro de comida.
     * @return DTO con la información de la comida registrada.
     */
    DiarioComidaResponseDTO registrarComida(DiarioComidaRequestDTO dto);

    /**
     * Actualiza un registro existente de comida respetando las reglas de la ventana de tiempo.
     *
     * @param id  Identificador único del registro a actualizar.
     * @param dto Nuevos datos del registro de comida.
     * @return DTO con la información actualizada del registro.
     */
    DiarioComidaResponseDTO actualizarComida(Long id, DiarioComidaRequestDTO dto);

    /**
     * Recupera el historial completo de comidas de un paciente específico ordenado cronológicamente.
     *
     * @param pacienteId Identificador único del paciente a consultar.
     * @return Lista de registros del diario de comidas del paciente.
     */
    List<DiarioComidaResponseDTO> obtenerHistorialPorPaciente(Long pacienteId);

    /**
     * Permite al nutriólogo añadir o actualizar una observación profesional en un registro de comida.
     *
     * @param id  Identificador único del registro a retroalimentar.
     * @param dto DTO con el texto de la observación clínica.
     * @return DTO con el registro actualizado conteniendo la observación.
     */
    DiarioComidaResponseDTO agregarObservacion(Long id, ObservacionRequestDTO dto);

    /**
     * Elimina permanentemente un registro de comida del diario.
     *
     * @param id Identificador único del registro a eliminar.
     */
    void eliminarRegistro(Long id);
}