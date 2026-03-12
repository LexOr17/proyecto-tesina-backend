package com.upemor.app_nutriologo.usuario.service;

import com.upemor.app_nutriologo.correo.service.EmailService;
import com.upemor.app_nutriologo.exceptions.DuplicateResourceException;
import com.upemor.app_nutriologo.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.usuario.dto.UsuarioNombreDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.mapper.UsuarioMapper;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import com.upemor.app_nutriologo.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        String contrasenaGenerada = PasswordGenerator.generarContrasena();
        entidad.setPassword(passwordEncoder.encode(contrasenaGenerada));

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
    @Transactional(readOnly = true)
    public UsuarioNombreDTO obtenerNombreUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el id: " + id));
        
        return UsuarioNombreDTO.builder()
                .nombre(usuario.getNombre())
                .apellidoPaterno(usuario.getApellidoPaterno())
                .apellidoMaterno(usuario.getApellidoMaterno())
                .build();
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

}
