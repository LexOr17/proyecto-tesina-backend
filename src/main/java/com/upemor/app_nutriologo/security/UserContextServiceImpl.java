package com.upemor.app_nutriologo.security;

import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserContextServiceImpl implements UserContextService {

    @Override
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    @Override
    public Usuario getCurrentUser() {
        return getCurrentAuthUser().getUsuario();
    }

    @Override
    public AuthUser getCurrentAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            !(authentication.getPrincipal() instanceof AuthUser)) {
            throw new ResourceNotFoundException("No se encontró un usuario autenticado en el contexto de seguridad");
        }
        
        return (AuthUser) authentication.getPrincipal();
    }

    public Set<String> getCurrentUserRoles() {
        return getCurrentAuthUser().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public boolean isAdmin() {
        // Buscamos "ROLE_ADMINISTRADOR" porque tu AuthUser concatena "ROLE_" + "ADMINISTRADOR"
        return getCurrentUserRoles().contains("ROLE_ADMINISTRADOR");
    }
}
