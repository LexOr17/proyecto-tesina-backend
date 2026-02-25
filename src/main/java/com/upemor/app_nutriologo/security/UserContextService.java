package com.upemor.app_nutriologo.security;

import com.upemor.app_nutriologo.usuario.entity.Usuario;

public interface UserContextService {
    
    /**
     * Obtiene el ID del usuario actualmente autenticado.
     * @return ID del usuario (Long)
     */
    Long getCurrentUserId();

    /**
     * Obtiene la entidad Usuario completa del usuario autenticado.
     * @return Entidad Usuario
     */
    Usuario getCurrentUser();
    
    /**
     * Obtiene el objeto AuthUser (UserDetails) del contexto de seguridad.
     * @return AuthUser
     */
    AuthUser getCurrentAuthUser();
}
