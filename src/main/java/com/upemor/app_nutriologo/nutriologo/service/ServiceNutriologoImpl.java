package com.upemor.app_nutriologo.nutriologo.service;

import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.nutriologo.dto.NutriologoSelectorDTO;
import com.upemor.app_nutriologo.nutriologo.repository.NutriologoRepository;
import com.upemor.app_nutriologo.paciente.dto.PacienteRequestDTO;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import com.upemor.app_nutriologo.security.UserContextService;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import com.upemor.app_nutriologo.usuario.enums.Rol;
import com.upemor.app_nutriologo.usuario.mapper.UsuarioMapper;
import com.upemor.app_nutriologo.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la capa de servicios para las operaciones exclusivas de los nutriólogos.
 * <p>
 * Gestiona el registro y tutela de pacientes asignados al profesional en sesión,
 * verificando los controles de acceso contextuales mediante {@link UserContextService}
 * para evitar accesos cruzados no autorizados entre diferentes nutriólogos.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ServiceNutriologoImpl implements ServiceNutriologo {

    private final NutriologoRepository nutriologoRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioService usuarioService;
    private final UserContextService userContextService;
    private final UsuarioMapper usuarioMapper;

    /**
     * Obtiene el listado de nutriólogos ordenados alfabéticamente para componentes selectores.
     *
     * @return lista de {@link NutriologoSelectorDTO}.
     */
    @Override
    @Transactional(readOnly = true)
    public List<NutriologoSelectorDTO> selectorDeNutriologos() {
        return nutriologoRepository.selectorNutriologo(Sort.by(Sort.Direction.ASC,"nombre"))
                .stream().toList();
    }

    /**
     * Da de alta un nuevo paciente vinculándolo automáticamente al identificador del nutriólogo en sesión.
     *
     * @param pacienteDTO datos del paciente a registrar.
     * @return {@link UsuarioResponseDTO} con los datos del paciente dado de alta.
     */
    @Override
    @Transactional
    public UsuarioResponseDTO registrarPaciente(PacienteRequestDTO pacienteDTO) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();
        pacienteDTO.setNutriologoId(idNutriologoLogueado);
        pacienteDTO.setRol(Rol.PACIENTE);
        return usuarioService.guardarUsuario(pacienteDTO);
    }

    /**
     * Recupera la página de pacientes asignados al nutriólogo en sesión o todos si el usuario es administrador.
     *
     * @param pageable configuración de paginación y ordenamiento.
     * @return página {@link Page} de {@link UsuarioResponseDTO}.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarPacientesAsignados(Pageable pageable) {
        if (userContextService.isAdmin()) {
            return pacienteRepository.findAll(pageable)
                    .map(usuarioMapper::toResponseDto);
        }
        Long idNutriologoLogueado = userContextService.getCurrentUserId();
        return pacienteRepository.findByNutriologoId(idNutriologoLogueado, pageable)
                .map(usuarioMapper::toResponseDto);
    }

    /**
     * Localiza a un paciente asignado verificando que pertenezca al nutriólogo en sesión o al propio paciente.
     *
     * @param idPaciente identificador primario del paciente.
     * @return {@link UsuarioResponseDTO} con los datos del paciente consultado.
     * @throws AccessDeniedException si el usuario no tiene permisos para consultar este paciente.
     * @throws ResourceNotFoundException si el paciente no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPacienteAsignado(Long idPaciente) {
        Long currentUserId = userContextService.getCurrentUserId();
        return pacienteRepository.findById(idPaciente)
                .map(paciente -> {
                    // El nutriólogo asignado al paciente puede ver los datos del paciente.
                    // El propio paciente puede ver sus propios datos.
                    if (!paciente.getNutriologo().getId().equals(currentUserId) && !paciente.getId().equals(currentUserId)) {
                        throw new AccessDeniedException("No tienes permiso para ver este paciente.");
                    }
                    return usuarioMapper.toResponseDto(paciente);
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente con el id: " + idPaciente));
    }

    /**
     * Actualiza los datos de un paciente verificando que pertenezca a la cartera del nutriólogo en sesión.
     *
     * @param idPaciente identificador primario del paciente a modificar.
     * @param pacienteDTO información actualizada del paciente.
     * @return {@link UsuarioResponseDTO} con los datos actualizados.
     * @throws AccessDeniedException si el paciente no está asignado al nutriólogo actual.
     * @throws ResourceNotFoundException si el paciente no existe.
     */
    @Override
    @Transactional
    public UsuarioResponseDTO actualizarPacienteAsignado(Long idPaciente, PacienteRequestDTO pacienteDTO) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();
        return pacienteRepository.findById(idPaciente)
                .map(paciente -> {
                    if (!paciente.getNutriologo().getId().equals(idNutriologoLogueado)) {
                        throw new AccessDeniedException("No tienes permiso para actualizar este paciente.");
                    }
                    // No se puede cambiar el nutriologoId ni el rol desde este método
                    return usuarioService.actualizarUsuario(idPaciente, pacienteDTO);
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente con el id: " + idPaciente));
    }

    /**
     * Elimina a un paciente asignado validando la autorización del nutriólogo en sesión.
     *
     * @param idPaciente identificador primario del paciente a eliminar.
     * @throws AccessDeniedException si el paciente no está asignado al nutriólogo en sesión.
     * @throws ResourceNotFoundException si el paciente no existe.
     */
    @Override
    @Transactional
    public void eliminarPacienteAsignado(Long idPaciente) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente con el id: " + idPaciente));
        if (!paciente.getNutriologo().getId().equals(idNutriologoLogueado)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este paciente.");
        }
        usuarioService.eliminarUsuario(idPaciente);
    }
}
