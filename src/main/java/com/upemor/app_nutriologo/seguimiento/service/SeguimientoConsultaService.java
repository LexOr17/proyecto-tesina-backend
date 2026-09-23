package com.upemor.app_nutriologo.seguimiento.service;

import com.upemor.app_nutriologo.seguimiento.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SeguimientoConsultaService {

    Page<SeguimientoConsultaDTO> listarPorPaciente(Long pacienteId, Pageable pageable);

    Page<SeguimientoConsultaDTO> listarMisSeguimientos(Pageable pageable);

    Page<SeguimientoConsultaDTO> listarTodos(Pageable pageable);

    SeguimientoConsultaDTO obtenerPorId(Long id);

    SeguimientoPreviewDTO generarPreview(Long pacienteId, LocalDate fechaInicio, LocalDate fechaFin);

    SeguimientoConsultaDTO crear(CrearSeguimientoRequestDTO request);

    SeguimientoConsultaDTO editarObservaciones(Long id, ActualizarObservacionesDTO request);

    void eliminar(Long id);
}
