package com.upemor.app_nutriologo.nutriologo.service;

import com.upemor.app_nutriologo.nutriologo.dto.NutriologoSelectorDTO;
import com.upemor.app_nutriologo.paciente.dto.PacienteRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Contrato de servicios para las operaciones exclusivas del rol de Nutriólogo.
 * <p>
 * Define la gestión de la cartera de pacientes asignados al profesional en sesión,
 * incluyendo registro, consultas autorizadas, modificaciones y listados para selectores.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface ServiceNutriologo {

    /**
     * Obtiene el catálogo ligero de nutriólogos para componentes de selección en la interfaz.
     *
     * @return lista de {@link NutriologoSelectorDTO}.
     */
    List<NutriologoSelectorDTO> selectorDeNutriologos();

    /**
     * Registra un nuevo paciente vinculándolo directamente al nutriólogo autenticado.
     *
     * @param pacienteDTO datos de la solicitud con la información del paciente.
     * @return {@link UsuarioResponseDTO} con los datos del paciente registrado.
     */
    UsuarioResponseDTO registrarPaciente(PacienteRequestDTO pacienteDTO);

    /**
     * Obtiene una página con los pacientes que se encuentran asignados al nutriólogo en sesión.
     *
     * @param pageable opciones de paginación y ordenamiento.
     * @return página {@link Page} de {@link UsuarioResponseDTO} con los pacientes del profesional.
     */
    Page<UsuarioResponseDTO> listarPacientesAsignados(Pageable pageable);

    /**
     * Localiza a un paciente asignado asegurando que pertenezca a la cartera del nutriólogo en sesión.
     *
     * @param idPaciente identificador primario del paciente a consultar.
     * @return {@link UsuarioResponseDTO} con los datos del paciente.
     */
    UsuarioResponseDTO buscarPacienteAsignado(Long idPaciente);

    /**
     * Actualiza la información de un paciente asignado al nutriólogo en sesión.
     *
     * @param idPaciente identificador primario del paciente a modificar.
     * @param pacienteDTO datos con las modificaciones del paciente.
     * @return {@link UsuarioResponseDTO} con la información actualizada.
     */
    UsuarioResponseDTO actualizarPacienteAsignado(Long idPaciente, PacienteRequestDTO pacienteDTO);

    /**
     * Elimina el registro de un paciente asignado al nutriólogo en sesión.
     *
     * @param idPaciente identificador primario del paciente a eliminar.
     */
    void eliminarPacienteAsignado(Long idPaciente);
}
