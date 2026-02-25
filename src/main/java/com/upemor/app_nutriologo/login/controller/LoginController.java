package com.upemor.app_nutriologo.login.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.login.dto.LoginRequestDTO;
import com.upemor.app_nutriologo.login.dto.LoginResponseDTO;
import com.upemor.app_nutriologo.login.dto.PasswordRecoveryRequestDTO;
import com.upemor.app_nutriologo.login.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = loginService.login(loginRequestDTO);

        ApiResponse<LoginResponseDTO> apiResponse = ApiResponse.<LoginResponseDTO>builder()
                .estatus(200)
                .mensaje("Inicio de sesión exitoso")
                .datos(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<ApiResponse<String>> recoverPassword(@RequestBody @Valid PasswordRecoveryRequestDTO passwordRecoveryRequestDTO) {
        loginService.recoverPassword(passwordRecoveryRequestDTO);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .estatus(200)
                .mensaje("Se ha enviado un correo electrónico con la nueva contraseña")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
