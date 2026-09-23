package com.upemor.app_nutriologo.security;

import com.upemor.app_nutriologo.common.exceptions.UnauthorizedException;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación del servicio {@link UserContextService}.
 * <p>
 * Recupera de forma segura los datos del usuario autenticado accediendo al
 * {@link SecurityContextHolder} de Spring Security y extrayendo la información del principal {@link AuthUser}.
 * </p>
 * 
 * @author Sistema Nutriólogo
 * @version 1.0
 * @see UserContextService
 * @see AuthUser
 * @see SecurityContextHolder
 */
@Service
@RequiredArgsConstructor
public class UserContextServiceImpl implements UserContextService {

    /**
     * Obtiene el identificador único (ID) del usuario autenticado en la petición actual.
     *
     * @return ID numérico del usuario.
     * @throws UnauthorizedException si no existe una sesión autenticada activa.
     */
    @Override
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Obtiene la entidad {@link Usuario} del usuario autenticado en la sesión actual.
     *
     * @return entidad {@link Usuario}.
     * @throws UnauthorizedException si no existe una sesión autenticada activa.
     */
    @Override
    public Usuario getCurrentUser() {
        return getCurrentAuthUser().getUsuario();
    }

    /**
     * Recupera y valida el principal {@link AuthUser} desde el contexto de Spring Security.
     *
     * @return objeto {@link AuthUser} con los datos del usuario autenticado.
     * @throws UnauthorizedException si no se encuentra autenticación activa o el principal no es de tipo {@link AuthUser}.
     */
    @Override
    public AuthUser getCurrentAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof AuthUser)) {
            throw new UnauthorizedException("No hay una sesión activa. Por favor, inicia sesión.");
        }

        return (AuthUser) authentication.getPrincipal();
    }

    /**
     * Obtiene el conjunto de roles o autoridades otorgadas al usuario autenticado.
     *
     * @return conjunto de cadenas con las autoridades del usuario (ej. {@code "ROLE_NUTRIOLOGO"}).
     * @throws UnauthorizedException si no existe una sesión autenticada activa.
     */
    @Override
    public Set<String> getCurrentUserRoles() {
        return getCurrentAuthUser().getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }

    /**
     * Valida si el usuario autenticado posee el rol de administrador ({@code ROLE_ADMINISTRADOR}).
     *
     * @return {@code true} si el usuario tiene asignado el rol {@code ROLE_ADMINISTRADOR}; {@code false} en caso contrario.
     * @throws UnauthorizedException si no existe una sesión autenticada activa.
     */
    @Override
    public boolean isAdmin() {
        // Buscamos "ROLE_ADMINISTRADOR" porque tu AuthUser concatena "ROLE_" +
        // "ADMINISTRADOR"
        return getCurrentUserRoles().contains("ROLE_ADMINISTRADOR");
    }
}

