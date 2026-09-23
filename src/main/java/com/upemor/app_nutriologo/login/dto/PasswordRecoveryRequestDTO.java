package com.upemor.app_nutriologo.login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para la solicitud de restablecimiento y recuperación de contraseñas.
 * <p>
 * Contiene la dirección de correo electrónico del usuario que solicita una nueva contraseña temporal.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Data
public class PasswordRecoveryRequestDTO {

    /**
     * Correo electrónico registrado de la cuenta a recuperar.
     */
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El formato del correo electrónico no es válido")
    private String email;
}
