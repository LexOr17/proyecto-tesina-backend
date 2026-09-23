package com.upemor.app_nutriologo.paciente.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO para la solicitud de registro o actualización de pacientes en el sistema.
 * <p>
 * Extiende de {@link UsuarioRequestDTO} añadiendo la referencia al nutriólogo
 * que será responsable de la atención del paciente.
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
public class PacienteRequestDTO extends UsuarioRequestDTO {

    /**
     * Identificador primario del nutriólogo asignado al paciente.
     */
    @JsonProperty("nutriologo_id")
    private Long nutriologoId;

}
