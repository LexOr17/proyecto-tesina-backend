package com.upemor.app_nutriologo.login.dto;

import com.upemor.app_nutriologo.usuario.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta emitido tras una autenticación exitosa en el sistema.
 * <p>
 * Transporta el token JWT generado, el rol asignado, el tiempo de expiración y los datos básicos del usuario autenticado.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    /**
     * Token JWT de acceso emitido para autorizar peticiones subsecuentes.
     */
    private String token;

    /**
     * Rol del usuario autenticado dentro del sistema.
     */
    private Rol rol;

    /**
     * Fecha y hora en la que expira el token de acceso emitido.
     */
    private String expirationTime;

    /**
     * Nombre(s) de pila del usuario autenticado.
     */
    private String nombre;

    /**
     * Apellidos del usuario autenticado.
     */
    private String apellidos;

    /**
     * Identificador primario del usuario.
     */
    private String id;

}
