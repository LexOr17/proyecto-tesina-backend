package com.upemor.app_nutriologo.login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO que encapsula las credenciales de acceso para el inicio de sesión en el sistema.
 * <p>
 * Contiene el correo electrónico y la contraseña con sus respectivas validaciones de formato y longitud.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Data
public class LoginRequestDTO {

    /**
     * Dirección de correo electrónico asociada a la cuenta del usuario.
     */
    @Email
    @NotBlank
    private String correo;

    /**
     * Contraseña en texto plano para verificar la autenticación (entre 8 y 32 caracteres).
     */
    @Size(min = 8, max = 32)
    @NotBlank
    private String password;

}
