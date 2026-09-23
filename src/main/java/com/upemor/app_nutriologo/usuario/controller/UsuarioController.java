package com.upemor.app_nutriologo.usuario.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.usuario.dto.UsuarioNombreDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import com.upemor.app_nutriologo.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de usuarios del sistema.
 * <p>
 * Proporciona endpoints para el registro, listado paginado, búsqueda por filtros,
 * consulta por identificador, obtención ligera de nombres, actualización, eliminación
 * y modificación de estado activo de usuarios.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Registra un nuevo usuario en la plataforma.
     *
     * @param usuarioRequestDTO cuerpo de la petición con los datos del usuario a crear.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo el usuario creado y estado HTTP 201.
     */
    @PostMapping("/registro")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> crearUsuario(@Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO nuevoUsuario = usuarioService.guardarUsuario(usuarioRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(nuevoUsuario, "Usuario creado exitosamente"));
    }

    /**
     * Obtiene el listado paginado de todos los usuarios registrados.
     *
     * @param pageable configuración de paginación y ordenamiento.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo la página de usuarios.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Page<UsuarioResponseDTO>>> listarUsuarios(Pageable pageable) {
        Page<UsuarioResponseDTO> usuarios = usuarioService.listarUsuarios(pageable);
        return ResponseEntity.ok(ApiResponse.success(usuarios, "Listado de usuarios"));
    }

    /**
     * Busca usuarios mediante una palabra clave que coincide con su nombre, apellidos o correo.
     *
     * @param keyword texto o término de búsqueda.
     * @param pageable configuración de paginación y ordenamiento.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo los usuarios coincidentes.
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Page<UsuarioResponseDTO>>> buscarUsuarios(@RequestParam("keyword") String keyword, Pageable pageable) {
        Page<UsuarioResponseDTO> usuarios = usuarioService.buscarUsuariosPorPalabraClave(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(usuarios, "Resultados de búsqueda de usuarios"));
    }

    /**
     * Recupera el detalle completo de un usuario por su identificador primario.
     *
     * @param id identificador primario del usuario.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo los datos del usuario.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> obtenerUsuarioPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(ApiResponse.success(usuario, "Usuario encontrado"));
    }

    /**
     * Obtiene de forma ligera únicamente los nombres y apellidos de un usuario específico.
     *
     * @param id identificador primario del usuario.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo el DTO con el nombre.
     */
    @GetMapping("/{id}/nombre")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<UsuarioNombreDTO>> obtenerNombreUsuarioPorId(@PathVariable Long id) {
        UsuarioNombreDTO usuarioNombre = usuarioService.obtenerNombreUsuarioPorId(id);
        return ResponseEntity.ok(ApiResponse.success(usuarioNombre, "Nombre de usuario encontrado"));
    }

    /**
     * Actualiza la información personal y de contacto de un usuario existente.
     *
     * @param id identificador primario del usuario a actualizar.
     * @param usuarioRequestDTO datos actualizados del usuario.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo el usuario actualizado.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioRequestDTO);
        return ResponseEntity.ok(ApiResponse.success(usuarioActualizado, "Usuario actualizado exitosamente"));
    }

    /**
     * Elimina a un usuario del sistema a partir de su identificador primario.
     *
     * @param id identificador primario del usuario a eliminar.
     * @return {@link ResponseEntity} con código HTTP 204 No Content.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Habilita o deshabilita la cuenta de un usuario en el sistema.
     *
     * @param id identificador primario del usuario.
     * @param activo {@code true} para activar, {@code false} para desactivar.
     * @return {@link ResponseEntity} con {@link ApiResponse} indicando el resultado de la operación.
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(@PathVariable Long id, @RequestParam("activo") boolean activo) {
        usuarioService.cambiarEstado(id, activo);
        String mensaje = activo ? "Usuario activado exitosamente" : "Usuario desactivado exitosamente";
        return ResponseEntity.ok(ApiResponse.success(null, mensaje));
    }
}
