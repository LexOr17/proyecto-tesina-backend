package com.upemor.app_nutriologo.nutriologo.dto;

import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
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
public class NutriologoRequestDTO extends UsuarioRequestDTO {
    private String cedulaProfesional;
}
