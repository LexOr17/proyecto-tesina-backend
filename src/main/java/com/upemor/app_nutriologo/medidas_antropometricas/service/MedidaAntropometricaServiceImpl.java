package com.upemor.app_nutriologo.medidas_antropometricas.service;

import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.medidas_antropometricas.dto.MedidaAntropometricaRequestDTO;
import com.upemor.app_nutriologo.medidas_antropometricas.dto.MedidaAntropometricaResponseDTO;
import com.upemor.app_nutriologo.medidas_antropometricas.entity.MedidaAntropometrica;
import com.upemor.app_nutriologo.medidas_antropometricas.mapper.MedidaAntropometricaMapper;
import com.upemor.app_nutriologo.medidas_antropometricas.repository.MedidaAntropometricaRepository;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedidaAntropometricaServiceImpl implements MedidaAntropometricaService {

    private final MedidaAntropometricaRepository medidaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedidaAntropometricaMapper medidaMapper;

    @Override
    @Transactional
    public MedidaAntropometricaResponseDTO registrarMedida(Long pacienteId, MedidaAntropometricaRequestDTO requestDTO) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + pacienteId));

        MedidaAntropometrica medida = medidaMapper.toEntity(requestDTO);
        medida.setPaciente(paciente);

        MedidaAntropometrica guardada = medidaRepository.save(medida);

        return medidaMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedidaAntropometricaResponseDTO> obtenerHistorialPorPaciente(Long pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente no encontrado con id: " + pacienteId);
        }

        return medidaRepository.findByPacienteId(pacienteId).stream()
                .map(medidaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MedidaAntropometricaResponseDTO obtenerUltimaMedida(Long pacienteId) {
        List<MedidaAntropometrica> historial = medidaRepository.findByPacienteId(pacienteId);

        if (historial.isEmpty()) {
            throw new ResourceNotFoundException("No hay mediciones registradas para el paciente con id: " + pacienteId);
        }

        MedidaAntropometrica ultima = historial.get(historial.size() - 1);

        return medidaMapper.toResponseDto(ultima);
    }

    @Override
    @Transactional
    public MedidaAntropometricaResponseDTO actualizarMedida(Long medidaId, MedidaAntropometricaRequestDTO requestDTO) {
        MedidaAntropometrica medida = medidaRepository.findById(medidaId)
                .orElseThrow(() -> new ResourceNotFoundException("Medida no encontrada con id: " + medidaId));

        medidaMapper.updateEntityFromDto(requestDTO, medida);

        MedidaAntropometrica actualizada = medidaRepository.save(medida);

        return medidaMapper.toResponseDto(actualizada);
    }

    @Override
    @Transactional
    public void eliminarMedida(Long medidaId) {
        if (!medidaRepository.existsById(medidaId)) {
            throw new ResourceNotFoundException("Medida no encontrada con id: " + medidaId);
        }
        medidaRepository.deleteById(medidaId);
    }
}
