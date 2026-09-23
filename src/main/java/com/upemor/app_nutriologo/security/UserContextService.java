package com.upemor.app_nutriologo.security;

import com.upemor.app_nutriologo.usuario.entity.Usuario;

import java.util.Set;

/**
 * Interfaz de servicio para la gestión y consulta del contexto del usuario autenticado.
 * <p>
 * Proporciona métodos convenientes y desacoplados para acceder a la identidad,
 * entidad de base de datos, detalles de autenticación y roles del usuario que realiza la petición actual.
 * </p>
 * 
 * @author Sistema Nutriólogo
 * @version 1.0
 * @see AuthUser
 * @see Usuario
 */
public interface UserContextService {
    
    /**
     * Obtiene el identificador único (ID) del usuario actualmente autenticado en el sistema.
     *
     * @return ID numérico del usuario ({@link Long}).
     */
    Long getCurrentUserId();

    /**
     * Obtiene la entidad {@link Usuario} completa asociada a la sesión del usuario autenticado.
     *
     * @return instancia de la entidad {@link Usuario}.
     */
    Usuario getCurrentUser();
    
    /**
     * Obtiene el objeto principal de seguridad {@link AuthUser} presente en el contexto de Spring Security.
     *
     * @return objeto {@link AuthUser} con los datos del usuario autenticado.
     */
    AuthUser getCurrentAuthUser();

    /**
     * Obtiene el conjunto de nombres de roles o autoridades asignados al usuario autenticado.
     *
     * @return conjunto de cadenas con los nombres de las autoridades (ej. {@code "ROLE_ADMINISTRADOR"}).
     */
    Set<String> getCurrentUserRoles();

    /**
     * Determina si el usuario autenticado posee privilegios de administrador ({@code ROLE_ADMINISTRADOR}).
     *
     * @return {@code true} si el usuario tiene rol de administrador; {@code false} en caso contrario.
     */
    boolean isAdmin();
}

