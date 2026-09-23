package com.upemor.app_nutriologo.historial_medico.service;

import com.upemor.app_nutriologo.common.exceptions.InvalidOperationException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.historial_medico.dto.HistorialMedicoRequestDTO;
import com.upemor.app_nutriologo.historial_medico.dto.HistorialMedicoResponseDTO;
import com.upemor.app_nutriologo.historial_medico.entity.HistorialMedico;
import com.upemor.app_nutriologo.historial_medico.enums.EnfermedadEnum;
import com.upemor.app_nutriologo.historial_medico.enums.EstadoMedicamento;
import com.upemor.app_nutriologo.historial_medico.enums.SintomaEnum;
import com.upemor.app_nutriologo.historial_medico.mapper.HistorialMedicoMapper;
import com.upemor.app_nutriologo.historial_medico.repository.HistorialMedicoRepository;
import com.upemor.app_nutriologo.medidas_antropometricas.entity.MedidaAntropometrica;
import com.upemor.app_nutriologo.medidas_antropometricas.repository.MedidaAntropometricaRepository;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialMedicoServiceImpl implements HistorialMedicoService {

    private final HistorialMedicoRepository historialRepository;
    private final PacienteRepository pacienteRepository;
    private final MedidaAntropometricaRepository medidaRepository;
    private final HistorialMedicoMapper mapper;

    // =========================================================
    // CREATE
    // =========================================================

    @Override
    @Transactional
    public HistorialMedicoResponseDTO registrarHistorial(Long pacienteId, HistorialMedicoRequestDTO dto) {
        Paciente paciente = obtenerPaciente(pacienteId);

        validarEnfermedades(dto);
        validarSintomas(dto);
        validarMedicamento(dto);

        MedidaAntropometrica medida = resolverMedida(dto.medidaAntropometricaId(), pacienteId);

        HistorialMedico historial = HistorialMedico.builder()
                .enfermedades(normalizar(dto.enfermedades()))
                .otraEnfermedad(tieneOtra(dto.enfermedades()) ? dto.otraEnfermedad() : null)
                .alergias(dto.alergias())
                .sintomas(normalizar(dto.sintomas()))
                .otroSintoma(tieneOtro(dto.sintomas()) ? dto.otroSintoma() : null)
                .estadoMedicamento(dto.estadoMedicamento())
                .descripcionMedicamento(
                        dto.estadoMedicamento() != EstadoMedicamento.NUNCA
                                ? dto.descripcionMedicamento()
                                : null)
                .paciente(paciente)
                .medidaAntropometrica(medida)
                .build();

        return mapper.toDto(historialRepository.save(historial));
    }

    // =========================================================
    // READ
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<HistorialMedicoResponseDTO> obtenerHistorialesPorPaciente(Long pacienteId, Pageable pageable) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente no encontrado con id: " + pacienteId);
        }
        return historialRepository
                .findByPacienteIdOrderByFechaRegistroDesc(pacienteId, pageable)
                .map(mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public HistorialMedicoResponseDTO obtenerUltimoHistorial(Long pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente no encontrado con id: " + pacienteId);
        }
        return historialRepository.findTopByPacienteIdOrderByFechaRegistroDesc(pacienteId)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay historial médico registrado para el paciente con id: " + pacienteId));
    }

    @Override
    @Transactional(readOnly = true)
    public HistorialMedicoResponseDTO obtenerHistorialPorId(Long historialId) {
        return mapper.toDto(obtenerHistorial(historialId));
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    @Transactional
    public HistorialMedicoResponseDTO actualizarHistorial(Long historialId, HistorialMedicoRequestDTO dto) {
        HistorialMedico historial = obtenerHistorial(historialId);

        validarEnfermedades(dto);
        validarSintomas(dto);
        validarMedicamento(dto);

        MedidaAntropometrica medida = resolverMedida(
                dto.medidaAntropometricaId(),
                historial.getPaciente().getId());

        historial.setEnfermedades(normalizar(dto.enfermedades()));
        historial.setOtraEnfermedad(tieneOtra(dto.enfermedades()) ? dto.otraEnfermedad() : null);
        historial.setAlergias(dto.alergias());
        historial.setSintomas(normalizar(dto.sintomas()));
        historial.setOtroSintoma(tieneOtro(dto.sintomas()) ? dto.otroSintoma() : null);
        historial.setEstadoMedicamento(dto.estadoMedicamento());
        historial.setDescripcionMedicamento(
                dto.estadoMedicamento() != EstadoMedicamento.NUNCA
                        ? dto.descripcionMedicamento()
                        : null);
        historial.setMedidaAntropometrica(medida);

        return mapper.toDto(historialRepository.save(historial));
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Override
    @Transactional
    public void eliminarHistorial(Long historialId) {
        if (!historialRepository.existsById(historialId)) {
            throw new ResourceNotFoundException("Historial médico no encontrado con id: " + historialId);
        }
        historialRepository.deleteById(historialId);
    }

    // =========================================================
    // Métodos privados de apoyo
    // =========================================================

    private Paciente obtenerPaciente(Long pacienteId) {
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente no encontrado con id: " + pacienteId));
    }

    private HistorialMedico obtenerHistorial(Long historialId) {
        return historialRepository.findById(historialId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Historial médico no encontrado con id: " + historialId));
    }

    /**
     * Devuelve la lista de enums o una lista vacía si viene null.
     */
    private <T> List<T> normalizar(List<T> lista) {
        return lista != null ? lista : new ArrayList<>();
    }

    /** Verifica si la lista de enfermedades incluye la opción OTRA. */
    private boolean tieneOtra(List<EnfermedadEnum> lista) {
        return lista != null && lista.contains(EnfermedadEnum.OTRA);
    }

    /** Verifica si la lista de síntomas incluye la opción OTRO. */
    private boolean tieneOtro(List<SintomaEnum> lista) {
        return lista != null && lista.contains(SintomaEnum.OTRO);
    }

    /**
     * Valida que si se selecciona EnfermedadEnum.OTRA se proporcione la descripción.
     */
    private void validarEnfermedades(HistorialMedicoRequestDTO dto) {
        if (tieneOtra(dto.enfermedades())) {
            if (dto.otraEnfermedad() == null || dto.otraEnfermedad().isBlank()) {
                throw new InvalidOperationException(
                        "Debe describir la enfermedad cuando selecciona la opción OTRA.");
            }
        }
    }

    /**
     * Valida que si se selecciona SintomaEnum.OTRO se proporcione la descripción.
     */
    private void validarSintomas(HistorialMedicoRequestDTO dto) {
        if (tieneOtro(dto.sintomas())) {
            if (dto.otroSintoma() == null || dto.otroSintoma().isBlank()) {
                throw new InvalidOperationException(
                        "Debe describir el síntoma cuando selecciona la opción OTRO.");
            }
        }
    }

    /**
     * Valida que si estadoMedicamento es ACTUALMENTE o ANTERIORMENTE
     * se proporcione la descripción del medicamento/suplemento.
     */
    private void validarMedicamento(HistorialMedicoRequestDTO dto) {
        if (dto.estadoMedicamento() != EstadoMedicamento.NUNCA) {
            boolean tieneDescripcion = dto.descripcionMedicamento() != null
                    && !dto.descripcionMedicamento().isBlank();
            if (!tieneDescripcion) {
                throw new InvalidOperationException(
                        "Debe proporcionar el nombre y dosis del medicamento o suplemento.");
            }
        }
    }

    /**
     * Resuelve la medida antropométrica a vincular:
     * - Si se envió un id explícito → valida que exista y pertenezca al paciente.
     * - Si no se envió → usa la medición más reciente del paciente (puede ser null).
     */
    private MedidaAntropometrica resolverMedida(Long medidaId, Long pacienteId) {
        if (medidaId != null) {
            MedidaAntropometrica medida = medidaRepository.findById(medidaId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Medida antropométrica no encontrada con id: " + medidaId));
            if (!medida.getPaciente().getId().equals(pacienteId)) {
                throw new AccessDeniedException(
                        "La medida antropométrica no pertenece al paciente indicado.");
            }
            return medida;
        }
        return medidaRepository
                .findTopByPacienteIdOrderByFechaMedicionDesc(pacienteId)
                .orElse(null);
    }
}
