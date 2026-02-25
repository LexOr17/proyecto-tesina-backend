package com.upemor.app_nutriologo.usuario.service;

import com.upemor.app_nutriologo.correo.service.EmailService;
import com.upemor.app_nutriologo.exceptions.DuplicateResourceException;
import com.upemor.app_nutriologo.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.mapper.UsuarioMapper;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    @Transactional
    public UsuarioResponseDTO guardarUsuario(UsuarioRequestDTO usuarioRequestDTO) {

        if (usuarioRepository.existsByCorreo(usuarioRequestDTO.getCorreo())) {
            throw new DuplicateResourceException("El correo " + usuarioRequestDTO.getCorreo() + " ya está en uso.");
        }

        if (usuarioRepository.existsByTelefono(usuarioRequestDTO.getTelefono())) {
            throw new DuplicateResourceException("El teléfono " + usuarioRequestDTO.getTelefono() + " ya está en uso.");
        }

        Usuario entidad = usuarioMapper.toEntity(usuarioRequestDTO);

//        // Lógica para asignar nutriólogo si el ID está presente en el DTO
//        if (usuarioRequestDTO.getNutriologoId() != null) {
//            Usuario nutriologo = usuarioRepository.findById(usuarioRequestDTO.getNutriologoId())
//                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró el nutriólogo con id: " + usuarioRequestDTO.getNutriologoId()));
//            entidad.setNutriologo(nutriologo);
//        }

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
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el id: " + id));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombre(usuarioRequestDTO.getNombre());
                    usuario.setApellidoPaterno(usuarioRequestDTO.getApellidoPaterno());
                    usuario.setApellidoMaterno(usuarioRequestDTO.getApellidoMaterno());
                    usuario.setTelefono(usuarioRequestDTO.getTelefono());
                    usuario.setFechaNacimiento(usuarioRequestDTO.getFechaNacimiento());
                    usuario.setGenero(usuarioRequestDTO.getGenero());

                    return usuarioMapper.toResponseDto(usuarioRepository.save(usuario));
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el id: " + id));
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró el usuario con el id: " + id);
        }
        usuarioRepository.deleteById(id);
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
