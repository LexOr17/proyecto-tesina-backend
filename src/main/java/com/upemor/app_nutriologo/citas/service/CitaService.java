package com.upemor.app_nutriologo.citas.service;

import com.upemor.app_nutriologo.citas.dto.CitaEstadoUpdateDTO;
import com.upemor.app_nutriologo.citas.dto.CitaRequestDTO;
import com.upemor.app_nutriologo.citas.dto.CitaResponseDTO;
import com.upemor.app_nutriologo.citas.dto.CitaSolicitudDTO;

import java.util.List;

/**
 * Contrato de servicios para la administración y lógica de negocio de citas médicas.
 * <p>
 * Define las operaciones disponibles para la creación, consulta, modificación,
 * eliminación, solicitud y transiciones de estado de las citas en el sistema.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface CitaService {

    /**
     * Crea y programa una nueva cita médica.
     *
     * @param citaRequestDTO datos requeridos para la creación de la cita.
     * @return {@link CitaResponseDTO} con los datos de la cita registrada.
     */
    CitaResponseDTO crearCita(CitaRequestDTO citaRequestDTO);

    /**
     * Registra una solicitud de cita generada por un paciente.
     *
     * @param pacienteId identificador del paciente que solicita la cita.
     * @param solicitudDTO datos con la fecha y hora solicitadas.
     * @return {@link CitaResponseDTO} con la información de la solicitud.
     */
    CitaResponseDTO solicitarCita(Long pacienteId, CitaSolicitudDTO solicitudDTO);

    /**
     * Recupera el listado completo de citas en el sistema.
     *
     * @return lista de {@link CitaResponseDTO}.
     */
    List<CitaResponseDTO> obtenerTodasLasCitas();

    /**
     * Obtiene una cita por su identificador único.
     *
     * @param id identificador primario de la cita.
     * @return {@link CitaResponseDTO} con los datos de la cita encontrada.
     */
    CitaResponseDTO obtenerCitaPorId(Long id);

    /**
     * Actualiza la información de una cita médica existente.
     *
     * @param id identificador de la cita a modificar.
     * @param citaRequestDTO datos actualizados de la cita.
     * @return {@link CitaResponseDTO} con la información actualizada.
     */
    CitaResponseDTO actualizarCita(Long id, CitaRequestDTO citaRequestDTO);

    /**
     * Elimina una cita médica según su identificador.
     *
     * @param id identificador de la cita a eliminar.
     */
    void eliminarCita(Long id);

    /**
     * Recupera las citas asociadas a un paciente específico.
     *
     * @param pacienteId identificador del paciente.
     * @return lista de citas correspondientes al paciente.
     */
    List<CitaResponseDTO> obtenerCitasPorPacienteId(Long pacienteId);

    /**
     * Recupera las citas asignadas a un nutriólogo específico.
     *
     * @param nutriologoId identificador del nutriólogo.
     * @return lista de citas correspondientes al nutriólogo.
     */
    List<CitaResponseDTO> obtenerCitasPorNutriologoId(Long nutriologoId);

    /**
     * Actualiza el estado del ciclo de vida de una cita médica.
     *
     * @param citaId identificador de la cita a actualizar.
     * @param estadoUpdateDTO datos con el nuevo estado y detalles adicionales.
     * @return {@link CitaResponseDTO} con el nuevo estado persistido.
     */
    CitaResponseDTO actualizarEstadoCita(Long citaId, CitaEstadoUpdateDTO estadoUpdateDTO);
}
