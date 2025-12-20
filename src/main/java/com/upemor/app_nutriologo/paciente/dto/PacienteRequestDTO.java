package com.upemor.app_nutriologo.paciente.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PacienteRequestDTO extends UsuarioRequestDTO {

    @JsonProperty("nutriologo_id")
    @NotNull(message = "El campo del nutriologo es es obligatorio")
    private Long nutriologoId;

}
