package com.upemor.app_nutriologo.login.service;

import com.upemor.app_nutriologo.login.dto.LoginRequestDTO;
import com.upemor.app_nutriologo.login.dto.LoginResponseDTO;
import com.upemor.app_nutriologo.login.dto.PasswordRecoveryRequestDTO;

public interface LoginService {

    LoginResponseDTO login(LoginRequestDTO request);

    void recoverPassword(PasswordRecoveryRequestDTO request);

}
