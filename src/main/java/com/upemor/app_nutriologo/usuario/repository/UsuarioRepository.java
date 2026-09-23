package com.upemor.app_nutriologo.usuario.repository;

import com.upemor.app_nutriologo.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad {@link Usuario}.
 * <p>
 * Proporciona métodos de consulta derivados y personalizados para la gestión,
 * autenticación, validación de unicidad y búsqueda de usuarios en la base de datos.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     *
     * @param correo dirección de correo electrónico asociada al usuario.
     * @return un {@link Optional} con el {@link Usuario} encontrado, o vacío si no existe.
     */
    Optional<Usuario> findByCorreo(String correo);

    /**
     * Comprueba si ya existe un usuario registrado con el correo electrónico proporcionado.
     *
     * @param correo dirección de correo electrónico a validar.
     * @return {@code true} si el correo ya se encuentra en uso, {@code false} en caso contrario.
     */
    boolean existsByCorreo(String correo);

    /**
     * Comprueba si ya existe un usuario registrado con el número telefónico proporcionado.
     *
     * @param telefono número telefónico a validar.
     * @return {@code true} si el número de teléfono ya se encuentra en uso, {@code false} en caso contrario.
     */
    boolean existsByTelefono(String telefono);

    /**
     * Realiza una búsqueda paginada de usuarios comparando una palabra clave
     * contra sus nombres, apellidos paterno/materno o correo electrónico de manera insensible a mayúsculas.
     *
     * @param keyword texto o término de búsqueda a consultar.
     * @param pageable configuración de paginación y ordenamiento.
     * @return una página {@link Page} de {@link Usuario} que coinciden con el término de búsqueda.
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.apellidoPaterno) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.apellidoMaterno) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Usuario> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Cuenta el total de usuarios que se encuentran con estado activo dentro del sistema.
     *
     * @return número total de usuarios activos registrados.
     */
    long countByActivoTrue();
}
