package com.upemor.app_nutriologo.nutriologo.dto;

import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
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
public class NutriologoResponseDTO extends UsuarioResponseDTO {
    private String cedulaProfesional;
}
