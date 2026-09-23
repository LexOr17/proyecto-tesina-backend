package com.upemor.app_nutriologo.medidas_antropometricas.service;

import com.upemor.app_nutriologo.medidas_antropometricas.dto.MedidaAntropometricaRequestDTO;
import com.upemor.app_nutriologo.medidas_antropometricas.dto.MedidaAntropometricaResponseDTO;

import java.util.List;

public interface MedidaAntropometricaService {

    MedidaAntropometricaResponseDTO registrarMedida(Long pacienteId, MedidaAntropometricaRequestDTO requestDTO);

    List<MedidaAntropometricaResponseDTO> obtenerHistorialPorPaciente(Long pacienteId);

    MedidaAntropometricaResponseDTO obtenerUltimaMedida(Long pacienteId);

    MedidaAntropometricaResponseDTO actualizarMedida(Long medidaId, MedidaAntropometricaRequestDTO requestDTO);

    void eliminarMedida(Long medidaId);
}
