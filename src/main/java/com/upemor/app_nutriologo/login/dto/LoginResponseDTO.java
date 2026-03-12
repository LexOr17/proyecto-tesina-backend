package com.upemor.app_nutriologo.login.dto;

import com.upemor.app_nutriologo.usuario.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String token;
    private Rol rol;
    private String expirationTime;
    private String nombre;
    private String apellidos;
    private String id;

}
