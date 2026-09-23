package com.upemor.app_nutriologo.administrador.dto;

import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO para la solicitud de creación y actualización de administradores en el sistema.
 * <p>
 * Extiende de {@link UsuarioRequestDTO} agregando la clasificación administrativa específica.
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
public class AdministradorRequestDTO extends UsuarioRequestDTO {

    /**
     * Clasificación o rol administrativo a asignar (ej. SUPER_ADMIN, SOPORTE).
     */
    private String tipoAdmin;
}
