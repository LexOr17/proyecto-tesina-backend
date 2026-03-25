package com.upemor.app_nutriologo.citas.service;

import com.upemor.app_nutriologo.citas.dto.CitaEstadoUpdateDTO;
import com.upemor.app_nutriologo.citas.dto.CitaRequestDTO;
import com.upemor.app_nutriologo.citas.dto.CitaResponseDTO;
import com.upemor.app_nutriologo.citas.dto.CitaSolicitudDTO;
import com.upemor.app_nutriologo.citas.entity.Cita;
import com.upemor.app_nutriologo.citas.enums.Estado;
import com.upemor.app_nutriologo.citas.mapper.CitaMapper;
import com.upemor.app_nutriologo.citas.repository.CitaRepository;
import com.upemor.app_nutriologo.common.exceptions.ScheduleConflictException;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.nutriologo.repository.NutriologoRepository;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

        validarDisponibilidadCita(nutriologo.getId(), citaRequestDTO.getFechaHoraInicio(), citaRequestDTO.getFechaHoraFin(), null);

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

        validarDisponibilidadCita(citaExistente.getNutriologo().getId(), citaRequestDTO.getFechaHoraInicio(), citaRequestDTO.getFechaHoraFin(), citaExistente.getId());

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
    public CitaResponseDTO actualizarEstadoCita(Long citaId, CitaEstadoUpdateDTO estadoUpdateDTO) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada con id: " + citaId));

        Estado nuevoEstado = estadoUpdateDTO.getNuevoEstado();

        switch (nuevoEstado) {
            case PROGRAMADA:
                if (cita.getEstado() == Estado.SOLICITADA) {
                    validarDisponibilidadCita(cita.getNutriologo().getId(), cita.getFechaHoraInicio(), cita.getFechaHoraFin(), cita.getId());
                    cita.setEstado(Estado.PROGRAMADA);
                } else if (cita.getEstado() == Estado.REPROGRAMACION_SOLICITADA) {
                    LocalDateTime nuevaFechaHoraInicio = cita.getFechaHoraReprogramacionSolicitada();
                    Objects.requireNonNull(nuevaFechaHoraInicio, "No se puede aceptar una reprogramación sin una nueva fecha propuesta.");
                    LocalDateTime nuevaFechaHoraFin = nuevaFechaHoraInicio.plusHours(1);

                    validarDisponibilidadCita(cita.getNutriologo().getId(), nuevaFechaHoraInicio, nuevaFechaHoraFin, cita.getId());

                    cita.setFechaHoraInicio(nuevaFechaHoraInicio);
                    cita.setFechaHoraFin(nuevaFechaHoraFin);
                    cita.setEstado(Estado.PROGRAMADA);
                    cita.setFechaHoraReprogramacionSolicitada(null); // Limpiar el campo
                } else {
                    throw new IllegalStateException("Solo se puede aceptar una cita que ha sido solicitada o reprogramada.");
                }
                break;
            case CANCELADA:
                cita.setEstado(Estado.CANCELADA);
                break;
            case CANCELACION_SOLICITADA:
                cita.setEstado(Estado.CANCELACION_SOLICITADA);
                cita.setDetalleAdicional(estadoUpdateDTO.getMotivo());
                break;
            case REPROGRAMACION_SOLICITADA:
                Objects.requireNonNull(estadoUpdateDTO.getNuevaFechaHoraInicio(), "La nueva fecha y hora de inicio no puede ser nula para reprogramar.");
                LocalDateTime nuevaFechaHoraFin = estadoUpdateDTO.getNuevaFechaHoraInicio().plusHours(1);
                validarDisponibilidadCita(cita.getNutriologo().getId(), estadoUpdateDTO.getNuevaFechaHoraInicio(), nuevaFechaHoraFin, cita.getId());
                cita.setEstado(Estado.REPROGRAMACION_SOLICITADA);
                cita.setFechaHoraReprogramacionSolicitada(estadoUpdateDTO.getNuevaFechaHoraInicio());
                break;
            case ASISTIO:
                cita.setEstado(Estado.ASISTIO);
                cita.setObservaciones(estadoUpdateDTO.getObservaciones());
                break;
            case NO_ASISTIO:
                cita.setEstado(Estado.NO_ASISTIO);
                cita.setObservaciones(null);
                break;
            default:
                throw new IllegalArgumentException("El estado " + nuevoEstado + " no es válido para esta operación.");
        }

        Cita citaActualizada = citaRepository.save(cita);
        return citaMapper.toResponseDTO(citaActualizada);
    }

    @Override
    @Transactional
    public CitaResponseDTO solicitarCita(Long pacienteId, CitaSolicitudDTO dto) {
        // 1. Recuperar entidades necesarias
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));

        Nutriologo nutriologo = paciente.getNutriologo();
        if (nutriologo == null) {
            throw new IllegalStateException("El paciente no tiene un nutriólogo asignado.");
        }

        // 2. Cálculos de negocio
        LocalDateTime fechaFin = dto.getFechaHoraInicio().plusHours(1);
        validarDisponibilidadCita(nutriologo.getId(), dto.getFechaHoraInicio(), fechaFin, null);

        // 3. Uso del Mapper
        Cita cita = citaMapper.toEntity(dto, paciente, nutriologo);

        // 4. Seteo de campos calculados/manuales
        cita.setFechaHoraFin(fechaFin);
        cita.setEstado(Estado.SOLICITADA);

        return citaMapper.toResponseDTO(citaRepository.save(cita));
    }

    private void validarDisponibilidadCita(Long nutriologoId, LocalDateTime inicio, LocalDateTime fin, Long citaIdExcluida) {
        // Usamos el repositorio para traer citas que NO estén canceladas
        List<Cita> citas = citaRepository.findAllByNutriologoId(nutriologoId);

        for (Cita existente : citas) {
            // 1. Ignorar la misma cita si estamos actualizando
            if (citaIdExcluida != null && existente.getId().equals(citaIdExcluida)) continue;

            // 2. Ignorar canceladas
            if (existente.getEstado() == Estado.CANCELADA) continue;

            // 3. Validar choque
            if (inicio.isBefore(existente.getFechaHoraFin()) && fin.isAfter(existente.getFechaHoraInicio())) {
                throw new ScheduleConflictException("El nutriólogo ya tiene una cita en ese horario ("
                        + existente.getFechaHoraInicio().toLocalTime() + " - " + existente.getFechaHoraFin().toLocalTime() + ")");
            }
        }
    }
}
