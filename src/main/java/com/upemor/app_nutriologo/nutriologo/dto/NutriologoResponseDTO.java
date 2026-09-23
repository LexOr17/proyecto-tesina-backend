package com.upemor.app_nutriologo.nutriologo.dto;

import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO de respuesta que expone la información pública del perfil de un nutriólogo.
 * <p>
 * Extiende de {@link UsuarioResponseDTO} añadiendo su número de cédula profesional.
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
public class NutriologoResponseDTO extends UsuarioResponseDTO {

    /**
     * Cédula profesional oficial del nutriólogo.
     */
    private String cedulaProfesional;
}
