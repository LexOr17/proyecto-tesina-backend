package com.upemor.app_nutriologo.usuario.service;

import com.upemor.app_nutriologo.usuario.dto.UsuarioNombreDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contrato de servicios para la administración y lógica de negocio de usuarios.
 * <p>
 * Define las operaciones CRUD, búsqueda paginada, consulta de nombres y control de estado
 * para los diferentes tipos de usuarios de la aplicación.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface UsuarioService {

    /**
     * Registra un nuevo usuario en el sistema validando restricciones de unicidad y credenciales.
     *
     * @param usuarioRequestDTO datos requeridos para la creación del usuario.
     * @return {@link UsuarioResponseDTO} con los datos del usuario registrado.
     */
    UsuarioResponseDTO guardarUsuario(UsuarioRequestDTO usuarioRequestDTO);

    /**
     * Recupera una lista paginada de todos los usuarios registrados en el sistema.
     *
     * @param pageable configuración de paginación y ordenamiento.
     * @return página {@link Page} de {@link UsuarioResponseDTO}.
     */
    Page<UsuarioResponseDTO> listarUsuarios(Pageable pageable);

    /**
     * Realiza una búsqueda paginada de usuarios basada en una palabra clave coincidente.
     *
     * @param keyword texto o término de búsqueda a consultar.
     * @param pageable configuración de paginación y ordenamiento.
     * @return página {@link Page} de {@link UsuarioResponseDTO} que coinciden con el criterio.
     */
    Page<UsuarioResponseDTO> buscarUsuariosPorPalabraClave(String keyword, Pageable pageable);

    /**
     * Consulta la información completa de un usuario por su identificador único.
     *
     * @param id identificador primario del usuario.
     * @return {@link UsuarioResponseDTO} con los datos del usuario encontrado.
     */
    UsuarioResponseDTO buscarUsuarioPorId(Long id);
    
    /**
     * Obtiene de forma ligera los componentes del nombre de un usuario específico.
     *
     * @param id identificador primario del usuario.
     * @return {@link UsuarioNombreDTO} con el nombre y apellidos del usuario.
     */
    UsuarioNombreDTO obtenerNombreUsuarioPorId(Long id);

    /**
     * Actualiza la información de un usuario existente en la base de datos.
     *
     * @param id identificador primario del usuario a modificar.
     * @param usuarioRequestDTO datos con las modificaciones a aplicar.
     * @return {@link UsuarioResponseDTO} con los datos actualizados.
     */
    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO);

    /**
     * Cambia el estado de activación de un usuario en el sistema.
     *
     * @param id identificador primario del usuario.
     * @param activo {@code true} para habilitar al usuario, {@code false} para suspenderlo.
     */
    void cambiarEstado(Long id, boolean activo);

    /**
     * Elimina a un usuario del sistema a partir de su identificador primario.
     *
     * @param id identificador primario del usuario a eliminar.
     */
    void eliminarUsuario(Long id);
}
