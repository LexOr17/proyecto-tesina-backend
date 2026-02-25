package com.upemor.app_nutriologo.nutriologo.service;

import com.upemor.app_nutriologo.nutriologo.dto.NutriologoSelectorDTO;
import com.upemor.app_nutriologo.nutriologo.repository.NutriologoRepository;
import com.upemor.app_nutriologo.paciente.dto.PacienteRequestDTO;
import com.upemor.app_nutriologo.security.UserContextService;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import com.upemor.app_nutriologo.usuario.enums.Rol;
import com.upemor.app_nutriologo.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceNutriologoImpl implements ServiceNutriologo {

    private final NutriologoRepository nutriologoRepository;
    private final UsuarioService usuarioService;
    private final UserContextService userContextService;

    @Override
    public List<NutriologoSelectorDTO> selectorDeNutriologos() {
        return nutriologoRepository.selectorNutriologo(Sort.by(Sort.Direction.ASC,"nombre"))
                .stream().toList();
    }

    @Override
    @Transactional
    public UsuarioResponseDTO registrarPaciente(PacienteRequestDTO pacienteDTO) {
        // Obtener el ID del Nutriólogo autenticado usando el servicio centralizado
        Long idNutriologoLogueado = userContextService.getCurrentUserId();

        // FORZAR la relación: El paciente es del nutriólogo que lo registra
        pacienteDTO.setNutriologoId(idNutriologoLogueado);

        // Asegurar que el rol sea PACIENTE
        pacienteDTO.setRol(Rol.PACIENTE);

        // Delegar el guardado al servicio de usuarios
        return usuarioService.guardarUsuario(pacienteDTO);
    }

}
