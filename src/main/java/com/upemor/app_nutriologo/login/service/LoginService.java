package com.upemor.app_nutriologo.login.service;

import com.upemor.app_nutriologo.login.dto.LoginRequestDTO;
import com.upemor.app_nutriologo.login.dto.LoginResponseDTO;

public interface LoginService {

    LoginResponseDTO login(LoginRequestDTO request);

}
