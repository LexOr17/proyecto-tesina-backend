package com.upemor.app_nutriologo.login.dto;

import com.upemor.app_nutriologo.usuario.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private Rol rol;
    private String expirationTime;

}
