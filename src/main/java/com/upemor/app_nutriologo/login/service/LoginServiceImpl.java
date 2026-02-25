package com.upemor.app_nutriologo.login.service;

import com.upemor.app_nutriologo.correo.service.EmailService;
import com.upemor.app_nutriologo.login.dto.LoginRequestDTO;
import com.upemor.app_nutriologo.login.dto.LoginResponseDTO;
import com.upemor.app_nutriologo.login.dto.PasswordRecoveryRequestDTO;
import com.upemor.app_nutriologo.security.AuthUser;
import com.upemor.app_nutriologo.security.JwtUtils;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import com.upemor.app_nutriologo.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCorreo(),
                        request.getContrasena()
                )
        );

        AuthUser userDetails = (AuthUser) authentication.getPrincipal();

        String token = jwtUtils.generateToken(userDetails);

        return new LoginResponseDTO(
                token,
                userDetails.getUsuario().getRol(),
                "8 horas");
    }

    @Override
    public void recoverPassword(PasswordRecoveryRequestDTO request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró un usuario con el correo electrónico proporcionado"));

        String newPassword = PasswordGenerator.generarContrasena();
        usuario.setContrasena(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        String emailContent = "Hola " + usuario.getNombre() + ",<br><br>" +
                "Tu nueva contraseña es: " + newPassword + "<br><br>" +
                "Te recomendamos que la cambies lo antes posible.<br><br>" +
                "Saludos,<br>" +
                "El equipo de Nutriólogos";

        emailService.sendEmail(new String[]{usuario.getCorreo()}, "Recuperación de contraseña", emailContent);
    }
}
