package com.upemor.app_nutriologo.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioNombreDTO {
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
}
