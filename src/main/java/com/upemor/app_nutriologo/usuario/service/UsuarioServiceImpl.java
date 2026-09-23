package com.upemor.app_nutriologo.usuario.service;

import com.upemor.app_nutriologo.correo.service.EmailService;
import com.upemor.app_nutriologo.correo.service.EmailTemplateService;
import com.upemor.app_nutriologo.common.exceptions.DuplicateResourceException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.notificaciones.enums.TipoNotificacion;
import com.upemor.app_nutriologo.notificaciones.service.NotificacionService;
import com.upemor.app_nutriologo.paciente.dto.PacienteRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioNombreDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.mapper.UsuarioMapper;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import com.upemor.app_nutriologo.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de la capa de servicios para la administración y ciclo de vida de usuarios.
 * <p>
 * Gestiona el registro con generación automática de contraseñas temporales, notificación por correo
 * y alertas en el sistema, control de unicidad de credenciales, búsquedas paginadas y actualización de perfiles.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;
    private final NotificacionService notificacionService;

    /**
     * Registra un nuevo usuario en la base de datos, valida la unicidad de correo y teléfono,
     * genera una contraseña temporal, la codifica y despacha las notificaciones pertinentes.
     *
     * @param usuarioRequestDTO datos del usuario a persistir.
     * @return {@link UsuarioResponseDTO} con los datos del usuario creado.
     * @throws DuplicateResourceException si el correo electrónico o el teléfono ya están en uso.
     */
    @Override
    @Transactional
    public UsuarioResponseDTO guardarUsuario(UsuarioRequestDTO usuarioRequestDTO) {

        if (usuarioRepository.existsByCorreo(usuarioRequestDTO.getCorreo())) {
            throw new DuplicateResourceException("El correo " + usuarioRequestDTO.getCorreo() + " ya está en uso.");
        }

        if (usuarioRepository.existsByTelefono(usuarioRequestDTO.getTelefono())) {
            throw new DuplicateResourceException("El teléfono " + usuarioRequestDTO.getTelefono() + " ya está en uso.");
        }

        Usuario entidad = usuarioMapper.toEntity(usuarioRequestDTO);

        String contrasenaGenerada = PasswordGenerator.generarContrasena();
        entidad.setPassword(passwordEncoder.encode(contrasenaGenerada));

        Usuario guardado = usuarioRepository.save(entidad);

        String asunto = "Credenciales de inicio de sesión";
        String contenidoHtml = emailTemplateService.renderCredencialesEmail(
                guardado.getNombre(), guardado.getCorreo(), contrasenaGenerada);

        emailService.sendHtmlEmail(new String[]{guardado.getCorreo()}, asunto, contenidoHtml);

        // Notificar al nutriólogo asignado cuando se le asigna un nuevo paciente
        if (usuarioRequestDTO instanceof PacienteRequestDTO pacienteDTO
                && pacienteDTO.getNutriologoId() != null) {
            notificacionService.enviarAUsuario(
                    pacienteDTO.getNutriologoId(),
                    TipoNotificacion.PACIENTE_ASIGNADO,
                    "Nuevo paciente asignado",
                    "Se te ha asignado un nuevo paciente: " + guardado.getNombre() + " " + guardado.getApellidoPaterno() + ".",
                    "/pacientes/" + guardado.getId()
            );
        }

        return usuarioMapper.toResponseDto(guardado);
    }

    /**
     * Obtiene una página con todos los usuarios registrados en el sistema.
     *
     * @param pageable opciones de paginación y ordenamiento.
     * @return página {@link Page} de {@link UsuarioResponseDTO}.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toResponseDto);
    }

    /**
     * Busca usuarios cuyo nombre, apellido o correo coincidan con la palabra clave dada.
     *
     * @param keyword término o texto de búsqueda.
     * @param pageable opciones de paginación y ordenamiento.
     * @return página {@link Page} de {@link UsuarioResponseDTO} coincidentes.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> buscarUsuariosPorPalabraClave(String keyword, Pageable pageable) {
        return usuarioRepository.searchByKeyword(keyword, pageable)
                .map(usuarioMapper::toResponseDto);
    }

    /**
     * Localiza a un usuario a partir de su identificador primario.
     *
     * @param id identificador único del usuario.
     * @return {@link UsuarioResponseDTO} del usuario encontrado.
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el id: " + id));
    }

    /**
     * Obtiene únicamente el nombre y apellidos de un usuario específico.
     *
     * @param id identificador primario del usuario.
     * @return {@link UsuarioNombreDTO} con los componentes de nombre del usuario.
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public UsuarioNombreDTO obtenerNombreUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el id: " + id));
        
        return UsuarioNombreDTO.builder()
                .nombre(usuario.getNombre())
                .apellidoPaterno(usuario.getApellidoPaterno())
                .apellidoMaterno(usuario.getApellidoMaterno())
                .build();
    }

    /**
     * Actualiza la información personal y de contacto de un usuario existente.
     *
     * @param id identificador primario del usuario a modificar.
     * @param usuarioRequestDTO datos con los nuevos valores a actualizar.
     * @return {@link UsuarioResponseDTO} con los datos actualizados del usuario.
     * @throws ResourceNotFoundException si el usuario no existe.
     * @throws DuplicateResourceException si el nuevo correo o teléfono ya están en uso por otra cuenta.
     */
    @Override
    @Transactional
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombre(usuarioRequestDTO.getNombre());
                    usuario.setApellidoPaterno(usuarioRequestDTO.getApellidoPaterno());
                    usuario.setApellidoMaterno(usuarioRequestDTO.getApellidoMaterno());
                    
                    if (!usuario.getCorreo().equals(usuarioRequestDTO.getCorreo())) {
                        if (usuarioRepository.existsByCorreo(usuarioRequestDTO.getCorreo())) {
                            throw new DuplicateResourceException("El correo " + usuarioRequestDTO.getCorreo() + " ya está en uso.");
                        }
                        usuario.setCorreo(usuarioRequestDTO.getCorreo());
                    }

                    if (!usuario.getTelefono().equals(usuarioRequestDTO.getTelefono())) {
                        if (usuarioRepository.existsByTelefono(usuarioRequestDTO.getTelefono())) {
                            throw new DuplicateResourceException("El teléfono " + usuarioRequestDTO.getTelefono() + " ya está en uso.");
                        }
                        usuario.setTelefono(usuarioRequestDTO.getTelefono());
                    }

                    usuario.setFechaNacimiento(usuarioRequestDTO.getFechaNacimiento());
                    usuario.setGenero(usuarioRequestDTO.getGenero());

                    return usuarioMapper.toResponseDto(usuarioRepository.save(usuario));
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el id: " + id));
    }

    /**
     * Elimina el registro de un usuario a partir de su identificador primario.
     *
     * @param id identificador único del usuario a eliminar.
     * @throws ResourceNotFoundException si no existe un usuario con el id provisto.
     */
    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró el usuario con el id: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Modifica el estado de habilitación o suspensión de un usuario.
     *
     * @param id identificador primario del usuario.
     * @param activo {@code true} para habilitar la cuenta, {@code false} para suspenderla.
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el id: " + id));
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
    }

}
