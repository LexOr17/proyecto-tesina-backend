package com.upemor.app_nutriologo.security;

import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio personalizado de carga de detalles de usuario para Spring Security.
 * <p>
 * Implementa la interfaz {@link UserDetailsService}, permitiendo al framework
 * consultar y cargar las credenciales y roles del usuario desde la base de datos
 * a través de su correo electrónico durante el flujo de autenticación y validación de tokens.
 * </p>
 * 
 * @author Sistema Nutriólogo
 * @version 1.0
 * @see UserDetailsService
 * @see AuthUser
 * @see UsuarioRepository
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Repositorio para la gestión y consulta de la entidad {@link Usuario}.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Localiza al usuario en la base de datos a partir de su nombre de usuario (correo electrónico).
     * <p>
     * Construye un objeto {@link AuthUser} que envuelve la entidad {@link Usuario} encontrada
     * para que Spring Security pueda verificar credenciales y permisos.
     * </p>
     *
     * @param username el correo electrónico del usuario que intenta autenticarse.
     * @return una instancia de {@link UserDetails} (específicamente {@link AuthUser}) con los datos del usuario.
     * @throws UsernameNotFoundException si no se encuentra ningún usuario registrado con el correo proporcionado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return AuthUser.builder()
                .usuario(usuario)
                .build();
    }

}