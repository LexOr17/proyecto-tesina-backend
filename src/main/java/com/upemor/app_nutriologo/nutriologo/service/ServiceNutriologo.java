package com.upemor.app_nutriologo.nutriologo.service;

import com.upemor.app_nutriologo.nutriologo.dto.NutriologoSelectorDTO;
import com.upemor.app_nutriologo.paciente.dto.PacienteRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServiceNutriologo {

    List<NutriologoSelectorDTO> selectorDeNutriologos();

    UsuarioResponseDTO registrarPaciente(PacienteRequestDTO pacienteDTO);

    Page<UsuarioResponseDTO> listarPacientesAsignados(Pageable pageable);

    UsuarioResponseDTO buscarPacienteAsignado(Long idPaciente);

    UsuarioResponseDTO actualizarPacienteAsignado(Long idPaciente, PacienteRequestDTO pacienteDTO);

    void eliminarPacienteAsignado(Long idPaciente);
}
