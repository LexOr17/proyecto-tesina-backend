package com.upemor.app_nutriologo.paciente.dto;

import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * DTO de respuesta con la información pública del perfil de un paciente.
 * <p>
 * Extiende de {@link UsuarioResponseDTO} incorporando los datos principales
 * del profesional de la nutrición asignado.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PacienteResponseDTO extends UsuarioResponseDTO {

    /**
     * Identificador primario del nutriólogo asignado.
     */
    private Long nutriologoId;

    /**
     * Nombre(s) de pila del nutriólogo asignado.
     */
    private String nombreNutriologo;

    /**
     * Primer apellido del nutriólogo asignado.
     */
    private String apellidoPaternoNutriologo;
}
