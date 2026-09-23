package com.upemor.app_nutriologo.login.service;

import com.upemor.app_nutriologo.login.dto.LoginRequestDTO;
import com.upemor.app_nutriologo.login.dto.LoginResponseDTO;
import com.upemor.app_nutriologo.login.dto.PasswordRecoveryRequestDTO;

/**
 * Contrato de servicios para los procesos de autenticación y recuperación de credenciales de acceso.
 * <p>
 * Proporciona métodos para validar identidades, expedir tokens JWT y gestionar el restablecimiento de contraseñas.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface LoginService {

    /**
     * Autentica a un usuario mediante sus credenciales y genera un token JWT con sus detalles de perfil.
     *
     * @param request DTO con el correo y contraseña del usuario.
     * @return {@link LoginResponseDTO} con el token de acceso, tiempo de expiración y datos del usuario.
     */
    LoginResponseDTO login(LoginRequestDTO request);

    /**
     * Genera una nueva contraseña temporal para el usuario y se la envía a su correo electrónico.
     *
     * @param request DTO con el correo electrónico del usuario solicitante.
     */
    void recoverPassword(PasswordRecoveryRequestDTO request);

}
