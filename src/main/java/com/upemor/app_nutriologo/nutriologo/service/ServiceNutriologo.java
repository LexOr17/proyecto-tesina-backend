package com.upemor.app_nutriologo.nutriologo.service;

import com.upemor.app_nutriologo.nutriologo.dto.NutriologoSelectorDTO;
import com.upemor.app_nutriologo.paciente.dto.PacienteRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;

import java.util.List;

public interface ServiceNutriologo {

    List<NutriologoSelectorDTO> selectorDeNutriologos();

    UsuarioResponseDTO registrarPaciente(PacienteRequestDTO pacienteDTO);
}
