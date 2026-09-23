package com.upemor.app_nutriologo.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado que transporta únicamente los componentes del nombre de un usuario.
 * <p>
 * Empleado para vistas ligeras, selectores o encabezados donde solo se requiere
 * desplegar el nombre y apellidos sin el resto de la información del perfil.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioNombreDTO {

    /**
     * Nombre o nombres de pila del usuario.
     */
    private String nombre;

    /**
     * Primer apellido (paterno) del usuario.
     */
    private String apellidoPaterno;

    /**
     * Segundo apellido (materno) del usuario, opcional.
     */
    private String apellidoMaterno;
}
