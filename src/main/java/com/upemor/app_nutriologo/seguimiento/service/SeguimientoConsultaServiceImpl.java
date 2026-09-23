package com.upemor.app_nutriologo.seguimiento.service;

import com.upemor.app_nutriologo.citas.entity.Cita;
import com.upemor.app_nutriologo.citas.enums.Estado;
import com.upemor.app_nutriologo.citas.repository.CitaRepository;
import com.upemor.app_nutriologo.common.exceptions.BadRequestException;
import com.upemor.app_nutriologo.common.exceptions.DuplicateResourceException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.diario_comidas.entity.DiarioComida;
import com.upemor.app_nutriologo.diario_comidas.enums.EstadoRegistro;
import com.upemor.app_nutriologo.diario_comidas.repository.DiarioComidaRepository;
import com.upemor.app_nutriologo.diario_extendido.entity.DiarioExtendido;
import com.upemor.app_nutriologo.diario_extendido.repository.DiarioExtendidoRepository;
import com.upemor.app_nutriologo.medidas_antropometricas.entity.MedidaAntropometrica;
import com.upemor.app_nutriologo.medidas_antropometricas.repository.MedidaAntropometricaRepository;
import com.upemor.app_nutriologo.meta.enums.EstadoMeta;
import com.upemor.app_nutriologo.meta.repository.MetaRepository;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.nutriologo.repository.NutriologoRepository;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import com.upemor.app_nutriologo.security.UserContextService;
import com.upemor.app_nutriologo.seguimiento.dto.*;
import com.upemor.app_nutriologo.seguimiento.entity.SeguimientoConsulta;
import com.upemor.app_nutriologo.seguimiento.mapper.SeguimientoMapper;
import com.upemor.app_nutriologo.seguimiento.repository.SeguimientoConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeguimientoConsultaServiceImpl implements SeguimientoConsultaService {

    private final SeguimientoConsultaRepository seguimientoRepository;
    private final PacienteRepository pacienteRepository;
    private final NutriologoRepository nutriologoRepository;
    private final DiarioComidaRepository diarioComidaRepository;
    private final DiarioExtendidoRepository diarioExtendidoRepository;
    private final MedidaAntropometricaRepository medidaRepository;
    private final MetaRepository metaRepository;
    private final CitaRepository citaRepository;
    private final UserContextService userContextService;
    private final SeguimientoMapper seguimientoMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<SeguimientoConsultaDTO> listarPorPaciente(Long pacienteId, Pageable pageable) {
        validarAccesoLecturaPaciente(pacienteId);
        return seguimientoRepository.findByPacienteIdOrderByFechaConsultaDesc(pacienteId, pageable)
                .map(seguimientoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SeguimientoConsultaDTO> listarMisSeguimientos(Pageable pageable) {
        Long currentUserId = userContextService.getCurrentUserId();
        return seguimientoRepository.findByPacienteIdOrderByFechaConsultaDesc(currentUserId, pageable)
                .map(seguimientoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SeguimientoConsultaDTO> listarTodos(Pageable pageable) {
        if (!userContextService.isAdmin()) {
            throw new AccessDeniedException("Solo administradores pueden listar todos los seguimientos.");
        }
        return seguimientoRepository.findAllByOrderByFechaConsultaDesc(pageable)
                .map(seguimientoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public SeguimientoConsultaDTO obtenerPorId(Long id) {
        SeguimientoConsulta consulta = seguimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seguimiento no encontrado con ID: " + id));
        validarAccesoLecturaConsulta(consulta);
        return seguimientoMapper.toDto(consulta);
    }

    @Override
    @Transactional(readOnly = true)
    public SeguimientoPreviewDTO generarPreview(Long pacienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        validarRangoFechas(fechaInicio, fechaFin);
        validarNutriologoAsignado(pacienteId);
        return calcularKPIs(pacienteId, fechaInicio, fechaFin);
    }

    @Override
    @Transactional
    public SeguimientoConsultaDTO crear(CrearSeguimientoRequestDTO request) {
        validarRangoFechas(request.fechaInicio(), request.fechaFin());
        validarNutriologoAsignado(request.pacienteId());

        if (seguimientoRepository.existsByPacienteIdAndFechaInicioAndFechaFin(
                request.pacienteId(), request.fechaInicio(), request.fechaFin())) {
            throw new DuplicateResourceException(
                    "Ya existe una consulta de seguimiento para este paciente en el período seleccionado (" +
                            request.fechaInicio() + " al " + request.fechaFin() + ").");
        }

        Paciente paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + request.pacienteId()));

        Long nutriologoId = userContextService.getCurrentUserId();
        Nutriologo nutriologo = nutriologoRepository.findById(nutriologoId)
                .orElseThrow(() -> new ResourceNotFoundException("Nutriólogo no encontrado con ID: " + nutriologoId));

        SeguimientoPreviewDTO kpis = calcularKPIs(request.pacienteId(), request.fechaInicio(), request.fechaFin());

        SeguimientoConsulta consulta = SeguimientoConsulta.builder()
                .paciente(paciente)
                .nutriologo(nutriologo)
                .fechaInicio(request.fechaInicio())
                .fechaFin(request.fechaFin())
                .pesoInicio(kpis.getPesoInicio())
                .pesoFin(kpis.getPesoFin())
                .diferenciaPeso(kpis.getDiferenciaPeso())
                .totalComidasRegistradas(kpis.getTotalComidasRegistradas())
                .comidasCumplidas(kpis.getComidasCumplidas())
                .comidasModificadas(kpis.getComidasModificadas())
                .comidasSaltadas(kpis.getComidasSaltadas())
                .pctCumplimientoAlimentos(kpis.getPctCumplimientoAlimentos() != null ? BigDecimal.valueOf(kpis.getPctCumplimientoAlimentos()) : null)
                .diasActividadRealizada(kpis.getDiasActividadRealizada())
                .totalDiasExtendidos(kpis.getTotalDiasExtendidos())
                .pctActividad(kpis.getPctActividad() != null ? BigDecimal.valueOf(kpis.getPctActividad()) : null)
                .totalMetas(kpis.getTotalMetas())
                .metasCumplidas(kpis.getMetasCumplidas())
                .totalCitas(kpis.getTotalCitas())
                .citasCompletadas(kpis.getCitasCompletadas())
                .observaciones(request.observaciones())
                .conclusiones(request.conclusiones())
                .recomendaciones(request.recomendaciones())
                .build();

        SeguimientoConsulta guardado = seguimientoRepository.save(consulta);
        return seguimientoMapper.toDto(guardado);
    }

    @Override
    @Transactional
    public SeguimientoConsultaDTO editarObservaciones(Long id, ActualizarObservacionesDTO request) {
        SeguimientoConsulta consulta = seguimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seguimiento no encontrado con ID: " + id));

        validarNutriologoAsignado(consulta.getPaciente().getId());

        consulta.setObservaciones(request.observaciones());
        consulta.setConclusiones(request.conclusiones());
        consulta.setRecomendaciones(request.recomendaciones());

        SeguimientoConsulta actualizado = seguimientoRepository.save(consulta);
        return seguimientoMapper.toDto(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        SeguimientoConsulta consulta = seguimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seguimiento no encontrado con ID: " + id));

        if (userContextService.isAdmin()) {
            seguimientoRepository.delete(consulta);
            return;
        }

        // Si es nutriólogo, debe ser el asignado al paciente
        validarNutriologoAsignado(consulta.getPaciente().getId());
        seguimientoRepository.delete(consulta);
    }

    // ── Métodos Auxiliares ───────────────────────────────────────────────────

    private void validarRangoFechas(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            throw new BadRequestException("Las fechas de inicio y fin son obligatorias.");
        }
        if (fin.isBefore(inicio)) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
    }

    private void validarNutriologoAsignado(Long pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + pacienteId));
        Long currentUserId = userContextService.getCurrentUserId();
        if (paciente.getNutriologo() == null || !paciente.getNutriologo().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Solo el nutriólogo asignado al paciente puede realizar esta acción.");
        }
    }

    private void validarAccesoLecturaPaciente(Long pacienteId) {
        if (userContextService.isAdmin()) {
            return;
        }
        Long currentUserId = userContextService.getCurrentUserId();
        // Si el usuario es el paciente mismo
        if (currentUserId.equals(pacienteId)) {
            return;
        }
        // Si es el nutriólogo asignado
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + pacienteId));
        if (paciente.getNutriologo() != null && paciente.getNutriologo().getId().equals(currentUserId)) {
            return;
        }
        throw new AccessDeniedException("No tienes permiso para ver el seguimiento de este paciente.");
    }

    private void validarAccesoLecturaConsulta(SeguimientoConsulta consulta) {
        if (userContextService.isAdmin()) {
            return;
        }
        Long currentUserId = userContextService.getCurrentUserId();
        if (consulta.getPaciente().getId().equals(currentUserId)) {
            return;
        }
        if (consulta.getNutriologo().getId().equals(currentUserId)) {
            return;
        }
        throw new AccessDeniedException("No tienes permiso para acceder a este seguimiento.");
    }

    private SeguimientoPreviewDTO calcularKPIs(Long pacienteId, LocalDate inicio, LocalDate fin) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + pacienteId));

        // 1. Diarios de comidas
        List<DiarioComida> diarios = diarioComidaRepository
                .findByPacienteIdAndFechaBetweenOrderByFechaAsc(pacienteId, inicio, fin);

        int totalComidas = diarios.size();
        int cumplidas = (int) diarios.stream().filter(d -> d.getEstado() == EstadoRegistro.CUMPLIDO).count();
        int modificadas = (int) diarios.stream().filter(d -> d.getEstado() == EstadoRegistro.MODIFICADO).count();
        int saltadas = (int) diarios.stream().filter(d -> d.getEstado() == EstadoRegistro.SALTADA).count();
        double pctCumplimiento = totalComidas > 0 ? ((double) cumplidas / totalComidas) * 100.0 : 0.0;

        // 2. Diarios extendidos (bienestar / actividad)
        List<DiarioExtendido> diariosExtendidos = diarioExtendidoRepository
                .findByPacienteIdAndFechaBetweenOrderByFechaAsc(pacienteId, inicio, fin);

        int diasActividad = (int) diariosExtendidos.stream()
                .filter(d -> Boolean.TRUE.equals(d.getActividadRealizada())).count();
        int totalExtendidos = diariosExtendidos.size();
        double pctActividad = totalExtendidos > 0 ? ((double) diasActividad / totalExtendidos) * 100.0 : 0.0;

        // 3. Medidas antropométricas
        List<MedidaAntropometrica> medidas = medidaRepository
                .findByPacienteIdAndFechaMedicionBetweenOrderByFechaMedicionAsc(pacienteId, inicio, fin);

        BigDecimal pesoInicio = medidas.isEmpty() ? null : medidas.get(0).getPeso();
        BigDecimal pesoFin = medidas.isEmpty() ? null : medidas.get(medidas.size() - 1).getPeso();
        BigDecimal diferenciaPeso = (pesoInicio != null && pesoFin != null)
                ? pesoFin.subtract(pesoInicio) : null;

        // 4. Metas
        long totalMetas = metaRepository.countByPacienteIdAndEstado(pacienteId, EstadoMeta.PENDIENTE)
                + metaRepository.countByPacienteIdAndEstado(pacienteId, EstadoMeta.CUMPLIDA)
                + metaRepository.countByPacienteIdAndEstado(pacienteId, EstadoMeta.NO_CUMPLIDA);
        long metasCumplidas = metaRepository.countByPacienteIdAndEstado(pacienteId, EstadoMeta.CUMPLIDA);

        // 5. Citas
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(23, 59, 59);
        List<Cita> citas = citaRepository.findByPacienteIdAndRangoFechas(pacienteId, inicioDateTime, finDateTime);

        long totalCitas = citas.size();
        long citasCompletadas = citas.stream().filter(c -> c.getEstado() == Estado.ASISTIO).count();

        return SeguimientoPreviewDTO.builder()
                .pacienteId(pacienteId)
                .pacienteNombre(paciente.getNombre() + " " + paciente.getApellidoPaterno())
                .fechaInicio(inicio)
                .fechaFin(fin)
                .pesoInicio(pesoInicio)
                .pesoFin(pesoFin)
                .diferenciaPeso(diferenciaPeso)
                .totalComidasRegistradas(totalComidas)
                .comidasCumplidas(cumplidas)
                .comidasModificadas(modificadas)
                .comidasSaltadas(saltadas)
                .pctCumplimientoAlimentos(Math.round(pctCumplimiento * 100.0) / 100.0)
                .diasActividadRealizada(diasActividad)
                .totalDiasExtendidos(totalExtendidos)
                .pctActividad(Math.round(pctActividad * 100.0) / 100.0)
                .totalMetas(totalMetas)
                .metasCumplidas(metasCumplidas)
                .totalCitas(totalCitas)
                .citasCompletadas(citasCompletadas)
                .build();
    }
}
