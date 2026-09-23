package com.upemor.app_nutriologo.security;

import com.upemor.app_nutriologo.usuario.entity.Usuario;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adaptador de seguridad que implementa {@link UserDetails} para Spring Security.
 * <p>
 * Encapsula la entidad de dominio {@link Usuario} para proveer al contexto de seguridad
 * la información requerida de autenticación y autorización (credenciales, roles con
 * prefijo {@code ROLE_} y estado de la cuenta).
 * </p>
 * 
 * @author Sistema Nutriólogo
 * @version 1.0
 * @see UserDetails
 * @see Usuario
 */
@Data
@Builder
public class AuthUser implements UserDetails {

    /**
     * Entidad del usuario del sistema asociada a este principal de seguridad.
     */
    private Usuario usuario;

    /**
     * Retorna las autoridades otorgadas al usuario.
     * <p>
     * Mapea el rol del usuario (por ejemplo, {@code ADMINISTRADOR}, {@code NUTRIOLOGO},
     * {@code PACIENTE}) a una autoridad reconocida por Spring Security agregando el prefijo
     * {@code ROLE_} (ej. {@code ROLE_ADMINISTRADOR}).
     * </p>
     *
     * @return colección que contiene la autoridad ({@link GrantedAuthority}) correspondiente al rol del usuario.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
        );
    }

    /**
     * Obtiene la contraseña encriptada (hash) del usuario para la autenticación.
     *
     * @return contraseña almacenada del usuario.
     */
    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    /**
     * Obtiene el nombre de usuario utilizado para la autenticación.
     * <p>
     * En este sistema, el correo electrónico del usuario actúa como identificador principal/nombre de usuario.
     * </p>
     *
     * @return correo electrónico del usuario.
     */
    @Override
    public String getUsername() {
        return usuario.getCorreo();
    }

    /**
     * Indica si la cuenta del usuario ha expirado.
     *
     * @return {@code true} si la cuenta no ha expirado; {@code false} en caso contrario.
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * Indica si la cuenta del usuario está bloqueada o no.
     *
     * @return {@code true} indicando que la cuenta no está bloqueada.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales (contraseña) del usuario han expirado.
     *
     * @return {@code true} indicando que las credenciales son válidas y no han expirado.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si el usuario se encuentra habilitado y activo en el sistema.
     * <p>
     * Se evalúa a partir de la propiedad {@code activo} de la entidad {@link Usuario}.
     * </p>
     *
     * @return {@code true} si el usuario está activo; {@code false} si está inactivo o nulo.
     */
    @Override
    public boolean isEnabled() {
        return usuario.getActivo() != null ? usuario.getActivo() : false;
    }

}

