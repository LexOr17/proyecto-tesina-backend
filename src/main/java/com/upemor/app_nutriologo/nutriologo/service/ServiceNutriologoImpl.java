package com.upemor.app_nutriologo.nutriologo.service;

import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.nutriologo.dto.NutriologoSelectorDTO;
import com.upemor.app_nutriologo.nutriologo.repository.NutriologoRepository;
import com.upemor.app_nutriologo.paciente.dto.PacienteRequestDTO;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import com.upemor.app_nutriologo.security.UserContextService;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import com.upemor.app_nutriologo.usuario.enums.Rol;
import com.upemor.app_nutriologo.usuario.mapper.UsuarioMapper;
import com.upemor.app_nutriologo.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceNutriologoImpl implements ServiceNutriologo {

    private final NutriologoRepository nutriologoRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioService usuarioService;
    private final UserContextService userContextService;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<NutriologoSelectorDTO> selectorDeNutriologos() {
        return nutriologoRepository.selectorNutriologo(Sort.by(Sort.Direction.ASC,"nombre"))
                .stream().toList();
    }

    @Override
    @Transactional
    public UsuarioResponseDTO registrarPaciente(PacienteRequestDTO pacienteDTO) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();
        pacienteDTO.setNutriologoId(idNutriologoLogueado);
        pacienteDTO.setRol(Rol.PACIENTE);
        return usuarioService.guardarUsuario(pacienteDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarPacientesAsignados(Pageable pageable) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();
        return pacienteRepository.findByNutriologoId(idNutriologoLogueado, pageable)
                .map(usuarioMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPacienteAsignado(Long idPaciente) {
        Long currentUserId = userContextService.getCurrentUserId();
        return pacienteRepository.findById(idPaciente)
                .map(paciente -> {
                    // El nutriólogo asignado al paciente puede ver los datos del paciente.
                    // El propio paciente puede ver sus propios datos.
                    if (!paciente.getNutriologo().getId().equals(currentUserId) && !paciente.getId().equals(currentUserId)) {
                        throw new AccessDeniedException("No tienes permiso para ver este paciente.");
                    }
                    return usuarioMapper.toResponseDto(paciente);
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente con el id: " + idPaciente));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarPacienteAsignado(Long idPaciente, PacienteRequestDTO pacienteDTO) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();
        return pacienteRepository.findById(idPaciente)
                .map(paciente -> {
                    if (!paciente.getNutriologo().getId().equals(idNutriologoLogueado)) {
                        throw new AccessDeniedException("No tienes permiso para actualizar este paciente.");
                    }
                    // No se puede cambiar el nutriologoId ni el rol desde este método
                    return usuarioService.actualizarUsuario(idPaciente, pacienteDTO);
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente con el id: " + idPaciente));
    }

    @Override
    @Transactional
    public void eliminarPacienteAsignado(Long idPaciente) {
        Long idNutriologoLogueado = userContextService.getCurrentUserId();
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el paciente con el id: " + idPaciente));
        if (!paciente.getNutriologo().getId().equals(idNutriologoLogueado)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este paciente.");
        }
        usuarioService.eliminarUsuario(idPaciente);
    }
}
