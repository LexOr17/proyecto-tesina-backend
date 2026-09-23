package com.upemor.app_nutriologo.nutriologo.dto;

import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO para la solicitud de registro o modificación de nutriólogos en la plataforma.
 * <p>
 * Extiende de {@link UsuarioRequestDTO} agregando la cédula profesional debidamente validada.
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
public class NutriologoRequestDTO extends UsuarioRequestDTO {

    /**
     * Cédula profesional oficial del nutriólogo, compuesta de 8 dígitos numéricos.
     */
    @NotBlank(message = "La cédula profesional es obligatoria")
    @Pattern(regexp = "^[0-9]{8}$", message = "La cédula profesional debe tener exactamente 8 dígitos numéricos")
    private String cedulaProfesional;
}
