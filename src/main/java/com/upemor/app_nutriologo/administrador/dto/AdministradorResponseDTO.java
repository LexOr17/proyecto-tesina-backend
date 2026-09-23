package com.upemor.app_nutriologo.administrador.dto;

import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO de respuesta con la información pública del perfil de un administrador.
 * <p>
 * Extiende de {@link UsuarioResponseDTO} incorporando el tipo de administrador.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdministradorResponseDTO extends UsuarioResponseDTO {

    /**
     * Nivel o clasificación de administración del usuario.
     */
    private String tipoAdmin;
}
