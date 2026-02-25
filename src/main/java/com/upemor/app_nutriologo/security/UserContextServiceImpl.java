package com.upemor.app_nutriologo.security;

import com.upemor.app_nutriologo.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
}
