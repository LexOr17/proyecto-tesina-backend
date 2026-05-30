package com.upemor.app_nutriologo.login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @Email
    @NotBlank
    private String correo;

    @Size(min = 8, max = 32)
    @NotBlank
    private String password;

}
