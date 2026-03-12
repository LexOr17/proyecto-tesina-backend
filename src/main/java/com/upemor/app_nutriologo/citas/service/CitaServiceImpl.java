package com.upemor.app_nutriologo.citas.service;

import com.upemor.app_nutriologo.citas.dto.*;
import com.upemor.app_nutriologo.citas.entity.Cita;
import com.upemor.app_nutriologo.citas.enums.Estado;
import com.upemor.app_nutriologo.citas.mapper.CitaMapper;
import com.upemor.app_nutriologo.citas.repository.CitaRepository;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.nutriologo.repository.NutriologoRepository;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final NutriologoRepository nutriologoRepository;
    private final CitaMapper citaMapper;

    @Override
    @Transactional
    public CitaResponseDTO crearCita(CitaRequestDTO citaRequestDTO) {
        Paciente paciente = pacienteRepository.findById(citaRequestDTO.getPacienteId())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con id: " + citaRequestDTO.getPacienteId()));

        Nutriologo nutriologo = nutriologoRepository.findById(citaRequestDTO.getNutriologoId())
                .orElseThrow(() -> new EntityNotFoundException("Nutriólogo no encontrado con id: " + citaRequestDTO.getNutriologoId()));

        Cita cita = citaMapper.toEntity(citaRequestDTO, paciente, nutriologo);
        cita.setEstado(Estado.PROGRAMADA);

        Cita citaGuardada = citaRepository.save(cita);
        return citaMapper.toResponseDTO(citaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> obtenerTodasLasCitas() {
        List<Cita> citas = citaRepository.findAll();
        return citaMapper.toResponseDTOList(citas);
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponseDTO obtenerCitaPorId(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con id: " + id));
        return citaMapper.toResponseDTO(cita);
    }

    @Override
    @Transactional
    public CitaResponseDTO actualizarCita(Long id, CitaRequestDTO citaRequestDTO) {
        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con id: " + id));

        citaMapper.updateEntityFromDto(citaRequestDTO, citaExistente);

        // Si el nutriólogo confirma una reprogramación, limpiamos el campo de solicitud
        if (citaExistente.getEstado() == Estado.REPROGRAMACION_SOLICITADA) {
            citaExistente.setFechaHoraReprogramacionSolicitada(null);
        }

        Cita citaActualizada = citaRepository.save(citaExistente);
        return citaMapper.toResponseDTO(citaActualizada);
    }

    @Override
    @Transactional
    public void eliminarCita(Long id) {
        if (!citaRepository.existsById(id)) {
            throw new EntityNotFoundException("Cita no encontrada con id: " + id);
        }
        citaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> obtenerCitasPorPacienteId(Long pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new EntityNotFoundException("Paciente no encontrado con id: " + pacienteId);
        }
        List<Cita> citas = citaRepository.findAllByPacienteId(pacienteId);
        return citaMapper.toResponseDTOList(citas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> obtenerCitasPorNutriologoId(Long nutriologoId) {
        if (!nutriologoRepository.existsById(nutriologoId)) {
            throw new EntityNotFoundException("Nutriólogo no encontrado con id: " + nutriologoId);
        }
        List<Cita> citas = citaRepository.findAllByNutriologoId(nutriologoId);
        return citaMapper.toResponseDTOList(citas);
    }

    @Override
    @Transactional
    public CitaResponseDTO solicitarCita(Long pacienteId, CitaSolicitudDTO solicitudDTO) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con id: " + pacienteId));

        Nutriologo nutriologo = paciente.getNutriologo();
        if (nutriologo == null) {
            throw new IllegalStateException("El paciente no tiene un nutriólogo asignado.");
        }

        Cita cita = citaMapper.toEntityFromSolicitud(solicitudDTO, paciente, nutriologo);
        cita.setEstado(Estado.SOLICITADA);
        cita.setFechaHoraFin(solicitudDTO.getFechaHoraInicio().plusHours(1));

        Cita citaGuardada = citaRepository.save(cita);
        return citaMapper.toResponseDTO(citaGuardada);
    }

    @Override
    @Transactional
    public CitaResponseDTO cancelarCita(Long citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con id: " + citaId));

        cita.setEstado(Estado.CANCELADA);
        Cita citaCancelada = citaRepository.save(cita);
        return citaMapper.toResponseDTO(citaCancelada);
    }

    @Override
    @Transactional
    public CitaResponseDTO solicitarCancelacionCita(Long citaId, CitaCancelacionRequestDTO cancelacionRequestDTO) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con id: " + citaId));

        cita.setEstado(Estado.CANCELACION_SOLICITADA);
        cita.setDetalleAdicional(cancelacionRequestDTO.getMotivo());
        Cita citaCancelada = citaRepository.save(cita);
        return citaMapper.toResponseDTO(citaCancelada);
    }

    @Override
    @Transactional
    public CitaResponseDTO reprogramarCita(Long citaId, CitaReprogramarDTO reprogramarDTO) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con id: " + citaId));

        cita.setEstado(Estado.REPROGRAMACION_SOLICITADA);
        cita.setFechaHoraReprogramacionSolicitada(reprogramarDTO.getNuevaFechaHoraInicio());

        Cita citaReprogramada = citaRepository.save(cita);
        return citaMapper.toResponseDTO(citaReprogramada);
    }

    @Override
    @Transactional
    public CitaResponseDTO finalizarCita(Long citaId, FinalizarCitaDTO finalizarCitaDTO) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con id: " + citaId));

        if (finalizarCitaDTO.getAsistio()) {
            cita.setEstado(Estado.ASISTIO);
            // Guardamos las observaciones solo si el paciente asistió
            cita.setObservaciones(finalizarCitaDTO.getObservaciones());
        } else {
            cita.setEstado(Estado.NO_ASISTIO);
            // Opcional: Limpiar las observaciones si no asistió
            cita.setObservaciones(null);
        }

        Cita citaFinalizada = citaRepository.save(cita);
        return citaMapper.toResponseDTO(citaFinalizada);
    }
}
