package com.upemor.app_nutriologo.usuario.service;

import com.upemor.app_nutriologo.correo.service.EmailService;
import com.upemor.app_nutriologo.exceptions.DuplicateResourceException;
import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.mapper.UsuarioMapper;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public UsuarioResponseDTO guardarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        if (usuarioRepository.existsByCorreo(usuarioRequestDTO.getCorreo())) {
            throw new DuplicateResourceException("El correo " + usuarioRequestDTO.getCorreo() + " ya está en uso.");
        }

        Usuario entidad = usuarioMapper.toEntity(usuarioRequestDTO);

        String contrasenaGenerada = generarContrasena();
        entidad.setContrasena(passwordEncoder.encode(contrasenaGenerada));

        Usuario guardado = usuarioRepository.save(entidad);

        String asunto = "Credenciales de inicio de sesión";
        String contenido = "Hola " + guardado.getNombre() + ",\n\n" +
                "Se ha generado una nueva cuenta para ti.\n" +
                "Usuario: " + guardado.getCorreo() + "\n" +
                "Contraseña: " + contrasenaGenerada;

        emailService.sendEmail(new String[]{guardado.getCorreo()}, asunto, contenido);

        return usuarioMapper.toResponseDto(guardado);
    }

    @Override
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public static String generarContrasena() {
        final String MAYUS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String MINUS = "abcdefghijklmnopqrstuvwxyz";
        final String NUM = "0123456789";
        final String ESPEC = "!@#$%^&*()-_=+[]{};:,.<>?/";

        final String TODOS = MAYUS + MINUS + NUM + ESPEC;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(8);

        // Garantizar que tenga al menos uno de cada tipo
        password.append(MAYUS.charAt(random.nextInt(MAYUS.length())));
        password.append(MINUS.charAt(random.nextInt(MINUS.length())));
        password.append(NUM.charAt(random.nextInt(NUM.length())));
        password.append(ESPEC.charAt(random.nextInt(ESPEC.length())));

        // Completar hasta 8 caracteres
        for (int i = 4; i < 8; i++) {
            password.append(TODOS.charAt(random.nextInt(TODOS.length())));
        }

        // Mezclar los caracteres para que no queden en orden predecible
        return mezclar(password.toString(), random);
    }

    private static String mezclar(String input, SecureRandom random) {
        char[] arr = input.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            int j = random.nextInt(arr.length);
            char temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return new String(arr);
    }

}
