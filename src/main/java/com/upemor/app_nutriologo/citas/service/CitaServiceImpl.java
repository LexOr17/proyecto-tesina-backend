package com.upemor.app_nutriologo.citas.service;

import com.upemor.app_nutriologo.citas.dto.CitaEstadoUpdateDTO;
import com.upemor.app_nutriologo.citas.dto.CitaRequestDTO;
import com.upemor.app_nutriologo.citas.dto.CitaResponseDTO;
import com.upemor.app_nutriologo.citas.dto.CitaSolicitudDTO;
import com.upemor.app_nutriologo.citas.entity.Cita;
import com.upemor.app_nutriologo.citas.enums.Estado;
import com.upemor.app_nutriologo.citas.enums.MotivoCita;
import com.upemor.app_nutriologo.citas.mapper.CitaMapper;
import com.upemor.app_nutriologo.citas.repository.CitaRepository;
import com.upemor.app_nutriologo.common.exceptions.BadRequestException;
import com.upemor.app_nutriologo.common.exceptions.InvalidOperationException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.common.exceptions.ScheduleConflictException;
import com.upemor.app_nutriologo.correo.service.EmailService;
import com.upemor.app_nutriologo.correo.service.EmailTemplateService;
import com.upemor.app_nutriologo.notificaciones.enums.TipoNotificacion;
import com.upemor.app_nutriologo.notificaciones.service.NotificacionService;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.nutriologo.repository.NutriologoRepository;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementación del servicio de gestión y control de citas.
 * <p>
 * Esta clase encapsula la lógica de negocio para la programación, reprogramación,
 * validación de horarios (detección de conflictos), cambios de estado
 * en el ciclo de vida de la cita, persistencia transaccional y notificaciones por
 * WebSocket y correo electrónico.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final NutriologoRepository nutriologoRepository;
    private final CitaMapper citaMapper;
    private final NotificacionService notificacionService;
    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;

    private static final DateTimeFormatter FORMATTER_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATTER_HORA = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Registra y programa una nueva cita en el sistema.
     * <p>
     * Valida la existencia del paciente y del nutriólogo, comprueba la disponibilidad
     * de agenda evitando colisiones de horario y persiste la entidad con estado inicial {@code PROGRAMADA}.
     * Emite notificación interna y envía correo de confirmación al paciente.
     * </p>
     *
     * @param citaRequestDTO datos de transferencia con la información requerida para crear la cita.
     * @return objeto {@link CitaResponseDTO} con los detalles de la cita creada.
     * @throws ResourceNotFoundException si el paciente o nutriólogo especificado no existe.
     * @throws ScheduleConflictException si el nutriólogo ya cuenta con una cita en el rango de horario.
     */
    @Override
    @Transactional
    public CitaResponseDTO crearCita(CitaRequestDTO citaRequestDTO) {
        Paciente paciente = pacienteRepository.findById(citaRequestDTO.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + citaRequestDTO.getPacienteId()));

        Nutriologo nutriologo = nutriologoRepository.findById(citaRequestDTO.getNutriologoId())
                .orElseThrow(() -> new ResourceNotFoundException("Nutriólogo no encontrado con id: " + citaRequestDTO.getNutriologoId()));

        validarDisponibilidadCita(nutriologo.getId(), citaRequestDTO.getFechaHoraInicio(), citaRequestDTO.getFechaHoraFin(), null);

        Cita cita = citaMapper.toEntity(citaRequestDTO, paciente, nutriologo);
        cita.setEstado(Estado.PROGRAMADA);

        Cita citaGuardada = citaRepository.save(cita);

        // 1. Notificar al paciente en la aplicación
        String fechaHoraStr = citaGuardada.getFechaHoraInicio().format(FORMATTER_FECHA_HORA);
        notificacionService.enviarAUsuario(
                paciente.getId(),
                TipoNotificacion.CITA_ACTUALIZADA,
                "Nueva cita programada",
                "Tu nutriólogo ha agendado una cita para el " + fechaHoraStr + ".",
                "/main/mis-citas"
        );

        // 2. Enviar correo electrónico de confirmación al paciente
        enviarCorreoCitaConfirmada(paciente, nutriologo, citaGuardada);

        return citaMapper.toResponseDTO(citaGuardada);
    }

    /**
     * Recupera el listado completo de todas las citas registradas en la base de datos.
     *
     * @return lista de objetos {@link CitaResponseDTO} con la información de cada cita.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> obtenerTodasLasCitas() {
        List<Cita> citas = citaRepository.findAll();
        return citaMapper.toResponseDTOList(citas);
    }

    /**
     * Obtiene la información detallada de una cita específica a partir de su identificador único.
     *
     * @param id identificador primario de la cita a consultar.
     * @return objeto {@link CitaResponseDTO} con los datos de la cita encontrada.
     * @throws ResourceNotFoundException si no existe una cita con el identificador proporcionado.
     */
    @Override
    @Transactional(readOnly = true)
    public CitaResponseDTO obtenerCitaPorId(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
        return citaMapper.toResponseDTO(cita);
    }

    /**
     * Actualiza los datos de una cita existente, verificando la compatibilidad de horarios.
     *
     * @param id identificador único de la cita a modificar.
     * @param citaRequestDTO datos actualizados de la cita.
     * @return objeto {@link CitaResponseDTO} con la información actualizada.
     * @throws ResourceNotFoundException si la cita a actualizar no existe.
     * @throws ScheduleConflictException si el nuevo horario colisiona con otra cita activa.
     */
    @Override
    @Transactional
    public CitaResponseDTO actualizarCita(Long id, CitaRequestDTO citaRequestDTO) {
        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));

        validarDisponibilidadCita(citaExistente.getNutriologo().getId(), citaRequestDTO.getFechaHoraInicio(), citaRequestDTO.getFechaHoraFin(), citaExistente.getId());

        citaMapper.updateEntityFromDto(citaRequestDTO, citaExistente);

        // Si el nutriólogo confirma una reprogramación, limpiamos el campo de solicitud
        if (citaExistente.getEstado() == Estado.REPROGRAMACION_SOLICITADA) {
            citaExistente.setFechaHoraReprogramacionSolicitada(null);
        }

        Cita citaActualizada = citaRepository.save(citaExistente);
        return citaMapper.toResponseDTO(citaActualizada);
    }

    /**
     * Elimina físicamente una cita médica del repositorio de datos.
     *
     * @param id identificador de la cita a eliminar.
     * @throws ResourceNotFoundException si la cita no existe en la base de datos.
     */
    @Override
    @Transactional
    public void eliminarCita(Long id) {
        if (!citaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cita no encontrada con id: " + id);
        }
        citaRepository.deleteById(id);
    }

    /**
     * Obtiene el historial de citas asociadas a un paciente específico.
     *
     * @param pacienteId identificador único del paciente.
     * @return lista de citas correspondientes al paciente consultado.
     * @throws ResourceNotFoundException si el paciente no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> obtenerCitasPorPacienteId(Long pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Paciente no encontrado con id: " + pacienteId);
        }
        List<Cita> citas = citaRepository.findAllByPacienteId(pacienteId);
        return citaMapper.toResponseDTOList(citas);
    }

    /**
     * Obtiene el listado de citas asignadas a la agenda de un nutriólogo específico.
     *
     * @param nutriologoId identificador único del nutriólogo.
     * @return lista de citas asociadas al nutriólogo consultado.
     * @throws ResourceNotFoundException si el nutriólogo no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> obtenerCitasPorNutriologoId(Long nutriologoId) {
        if (!nutriologoRepository.existsById(nutriologoId)) {
            throw new ResourceNotFoundException("Nutriólogo no encontrado con id: " + nutriologoId);
        }
        List<Cita> citas = citaRepository.findAllByNutriologoId(nutriologoId);
        return citaMapper.toResponseDTOList(citas);
    }

    /**
     * Gestiona y procesa las transiciones de estado en el flujo de una cita clínica.
     * <p>
     * Maneja eventos tales como aceptación de solicitudes, cancelaciones con motivo,
     * registro de reprogramaciones, y confirmación de asistencia o inasistencia.
     * Emite notificaciones internas y correos electrónicos según corresponda.
     * </p>
     *
     * @param citaId identificador de la cita cuyo estado será actualizado.
     * @param estadoUpdateDTO objeto de transferencia con el nuevo estado y metadatos asociados.
     * @return objeto {@link CitaResponseDTO} con el estado y datos actualizados.
     * @throws ResourceNotFoundException si la cita no existe.
     * @throws BadRequestException si los datos requeridos para la transición de estado no son válidos.
     * @throws InvalidOperationException si la transición de estado solicitada no es permitida.
     * @throws ScheduleConflictException si al reprogramar o aceptar la cita se detecta conflicto de horario.
     */
    @Override
    @Transactional
    public CitaResponseDTO actualizarEstadoCita(Long citaId, CitaEstadoUpdateDTO estadoUpdateDTO) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + citaId));

        Estado nuevoEstado = estadoUpdateDTO.getNuevoEstado();

        switch (nuevoEstado) {
            case PROGRAMADA:
                if (cita.getEstado() == Estado.SOLICITADA) {
                    validarDisponibilidadCita(cita.getNutriologo().getId(), cita.getFechaHoraInicio(), cita.getFechaHoraFin(), cita.getId());
                    cita.setEstado(Estado.PROGRAMADA);
                } else if (cita.getEstado() == Estado.REPROGRAMACION_SOLICITADA) {
                    LocalDateTime nuevaFechaHoraInicio = cita.getFechaHoraReprogramacionSolicitada();
                    if (nuevaFechaHoraInicio == null) {
                        throw new BadRequestException("No se puede aceptar una reprogramación sin una nueva fecha propuesta.");
                    }
                    LocalDateTime nuevaFechaHoraFin = nuevaFechaHoraInicio.plusHours(1);

                    validarDisponibilidadCita(cita.getNutriologo().getId(), nuevaFechaHoraInicio, nuevaFechaHoraFin, cita.getId());

                    cita.setFechaHoraInicio(nuevaFechaHoraInicio);
                    cita.setFechaHoraFin(nuevaFechaHoraFin);
                    cita.setEstado(Estado.PROGRAMADA);
                    cita.setFechaHoraReprogramacionSolicitada(null);
                } else {
                    throw new InvalidOperationException("Solo se puede aceptar una cita que ha sido solicitada o reprogramada.");
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
                if (estadoUpdateDTO.getNuevaFechaHoraInicio() == null) {
                    throw new BadRequestException("La nueva fecha y hora de inicio no puede ser nula para reprogramar.");
                }
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
                throw new BadRequestException("El estado " + nuevoEstado + " no es válido para esta operación.");
        }

        Cita citaActualizada = citaRepository.save(cita);

        // Notificar al paciente sobre el cambio de estado relevante (Aceptada / Cancelada)
        String fechaHoraStr = citaActualizada.getFechaHoraInicio().format(FORMATTER_FECHA_HORA);

        if (nuevoEstado == Estado.PROGRAMADA) {
            notificacionService.enviarAUsuario(
                    citaActualizada.getPaciente().getId(),
                    TipoNotificacion.CITA_ACTUALIZADA,
                    "Cita aceptada",
                    "Tu cita para el " + fechaHoraStr + " ha sido aceptada y programada.",
                    "/main/mis-citas"
            );
            // Enviar correo de confirmación de cita aceptada/reprogramada
            enviarCorreoCitaConfirmada(citaActualizada.getPaciente(), citaActualizada.getNutriologo(), citaActualizada);

        } else if (nuevoEstado == Estado.CANCELADA) {
            String motivo = estadoUpdateDTO.getMotivo() != null && !estadoUpdateDTO.getMotivo().isBlank()
                    ? " Motivo: " + estadoUpdateDTO.getMotivo()
                    : "";
            notificacionService.enviarAUsuario(
                    citaActualizada.getPaciente().getId(),
                    TipoNotificacion.CITA_ACTUALIZADA,
                    "Cita cancelada",
                    "Tu cita del " + fechaHoraStr + " ha sido cancelada." + motivo,
                    "/main/mis-citas"
            );
            // Enviar correo de cita cancelada al paciente
            enviarCorreoCitaCancelada(citaActualizada.getPaciente(), citaActualizada, estadoUpdateDTO.getMotivo());
        }

        return citaMapper.toResponseDTO(citaActualizada);
    }

    /**
     * Registra una nueva solicitud de cita iniciada directamente por un paciente.
     * <p>
     * Comprueba la asignación del nutriólogo, calcula la hora estimada de culminación
     * de la sesión (duración de 1 hora), verifica la disponibilidad en agenda y
     * persiste la cita en estado {@code SOLICITADA}.
     * Emite notificación interna y correo de aviso al nutriólogo.
     * </p>
     *
     * @param pacienteId identificador del paciente que realiza la solicitud.
     * @param dto datos de la solicitud con la fecha y hora de inicio deseadas.
     * @return objeto {@link CitaResponseDTO} con los detalles de la solicitud registrada.
     * @throws ResourceNotFoundException si el paciente no existe.
     * @throws InvalidOperationException si el paciente no tiene asignado un nutriólogo.
     * @throws ScheduleConflictException si el horario solicitado colisiona con otra cita activa.
     */
    @Override
    @Transactional
    public CitaResponseDTO solicitarCita(Long pacienteId, CitaSolicitudDTO dto) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + pacienteId));

        Nutriologo nutriologo = paciente.getNutriologo();
        if (nutriologo == null) {
            throw new InvalidOperationException("El paciente no tiene un nutriólogo asignado.");
        }

        // 2. Cálculos de negocio (duración predeterminada de consulta: 1 hora)
        LocalDateTime fechaFin = dto.getFechaHoraInicio().plusHours(1);
        validarDisponibilidadCita(nutriologo.getId(), dto.getFechaHoraInicio(), fechaFin, null);

        // 3. Conversión de DTO a entidad mediante MapStruct
        Cita cita = citaMapper.toEntity(dto, paciente, nutriologo);

        // 4. Configuración de campos de control y estado inicial
        cita.setFechaHoraFin(fechaFin);
        cita.setEstado(Estado.SOLICITADA);

        Cita citaGuardada = citaRepository.save(cita);

        // 5. Notificar al nutriólogo en la plataforma
        String fechaHoraStr = citaGuardada.getFechaHoraInicio().format(FORMATTER_FECHA_HORA);
        String pacienteNombre = construirNombreCompleto(paciente.getNombre(), paciente.getApellidoPaterno(), paciente.getApellidoMaterno());

        notificacionService.enviarAUsuario(
                nutriologo.getId(),
                TipoNotificacion.CITA_SOLICITADA,
                "Nueva solicitud de cita",
                pacienteNombre + " solicitó una cita para el " + fechaHoraStr + ".",
                "/main/citas"
        );

        // 6. Enviar correo electrónico al nutriólogo informando la solicitud
        enviarCorreoCitaSolicitada(nutriologo, paciente, citaGuardada);

        return citaMapper.toResponseDTO(citaGuardada);
    }

    /**
     * Valida si un rango de fechas y horas seleccionado se encuentra libre en la agenda del nutriólogo.
     *
     * @param nutriologoId identificador del nutriólogo cuya agenda se examina.
     * @param inicio fecha y hora de inicio de la cita propuesta.
     * @param fin fecha y hora de finalización estimada de la cita propuesta.
     * @param citaIdExcluida identificador de la cita a ignorar durante la validación (usado en actualizaciones).
     * @throws ScheduleConflictException si se detecta un traslape temporal con una cita existente.
     */
    private void validarDisponibilidadCita(Long nutriologoId, LocalDateTime inicio, LocalDateTime fin, Long citaIdExcluida) {
        List<Cita> citas = citaRepository.findAllByNutriologoId(nutriologoId);

        for (Cita existente : citas) {
            // 1. Ignorar la misma cita si se encuentra en proceso de edición/actualización
            if (citaIdExcluida != null && existente.getId().equals(citaIdExcluida)) {
                continue;
            }

            // 2. Ignorar citas con estado cancelado
            if (existente.getEstado() == Estado.CANCELADA) {
                continue;
            }

            // 3. Comprobar traslape temporal entre rangos horarios: [inicio, fin) vs [existente.inicio, existente.fin)
            if (inicio.isBefore(existente.getFechaHoraFin()) && fin.isAfter(existente.getFechaHoraInicio())) {
                throw new ScheduleConflictException("El nutriólogo ya tiene una cita en ese horario ("
                        + existente.getFechaHoraInicio().toLocalTime() + " - " + existente.getFechaHoraFin().toLocalTime() + ")");
            }
        }
    }

    // =========================================================================
    // Métodos auxiliares para envío de correos electrónicos de Citas
    // =========================================================================

    private void enviarCorreoCitaConfirmada(Paciente paciente, Nutriologo nutriologo, Cita cita) {
        if (paciente == null || paciente.getCorreo() == null || paciente.getCorreo().isBlank()) {
            return;
        }
        try {
            String pacienteNombre = construirNombreCompleto(paciente.getNombre(), paciente.getApellidoPaterno(), paciente.getApellidoMaterno());
            String nutriologoNombre = nutriologo != null
                    ? construirNombreCompleto(nutriologo.getNombre(), nutriologo.getApellidoPaterno(), nutriologo.getApellidoMaterno())
                    : "Nutriólogo(a)";
            String fechaHoraInicioStr = cita.getFechaHoraInicio().format(FORMATTER_FECHA_HORA);
            String fechaHoraFinStr = cita.getFechaHoraFin() != null ? cita.getFechaHoraFin().format(FORMATTER_HORA) : null;
            String motivoStr = formatearMotivoCita(cita.getMotivoCita());

            String contenidoHtml = emailTemplateService.renderCitaConfirmadaEmail(
                    pacienteNombre,
                    nutriologoNombre,
                    fechaHoraInicioStr,
                    fechaHoraFinStr,
                    motivoStr
            );

            emailService.sendHtmlEmail(
                    new String[]{paciente.getCorreo()},
                    "Cita confirmada y programada - NutriGestor",
                    contenidoHtml
            );
            log.info("Correo de cita confirmada enviado al paciente {}", paciente.getCorreo());
        } catch (Exception e) {
            log.warn("No se pudo enviar correo de confirmación de cita a {}: {}", paciente.getCorreo(), e.getMessage());
        }
    }

    private void enviarCorreoCitaCancelada(Paciente paciente, Cita cita, String motivoCancelacion) {
        if (paciente == null || paciente.getCorreo() == null || paciente.getCorreo().isBlank()) {
            return;
        }
        try {
            String pacienteNombre = construirNombreCompleto(paciente.getNombre(), paciente.getApellidoPaterno(), paciente.getApellidoMaterno());
            String fechaHoraInicioStr = cita.getFechaHoraInicio().format(FORMATTER_FECHA_HORA);
            String motivoStr = formatearMotivoCita(cita.getMotivoCita());

            String contenidoHtml = emailTemplateService.renderCitaCanceladaEmail(
                    pacienteNombre,
                    fechaHoraInicioStr,
                    motivoStr,
                    motivoCancelacion
            );

            emailService.sendHtmlEmail(
                    new String[]{paciente.getCorreo()},
                    "Cita cancelada - NutriGestor",
                    contenidoHtml
            );
            log.info("Correo de cita cancelada enviado al paciente {}", paciente.getCorreo());
        } catch (Exception e) {
            log.warn("No se pudo enviar correo de cancelación de cita a {}: {}", paciente.getCorreo(), e.getMessage());
        }
    }

    private void enviarCorreoCitaSolicitada(Nutriologo nutriologo, Paciente paciente, Cita cita) {
        if (nutriologo == null || nutriologo.getCorreo() == null || nutriologo.getCorreo().isBlank()) {
            return;
        }
        try {
            String nutriologoNombre = construirNombreCompleto(nutriologo.getNombre(), nutriologo.getApellidoPaterno(), nutriologo.getApellidoMaterno());
            String pacienteNombre = construirNombreCompleto(paciente.getNombre(), paciente.getApellidoPaterno(), paciente.getApellidoMaterno());
            String fechaHoraInicioStr = cita.getFechaHoraInicio().format(FORMATTER_FECHA_HORA);
            String motivoStr = formatearMotivoCita(cita.getMotivoCita());

            String contenidoHtml = emailTemplateService.renderCitaSolicitadaEmail(
                    nutriologoNombre,
                    pacienteNombre,
                    fechaHoraInicioStr,
                    motivoStr,
                    cita.getDetalleAdicional()
            );

            emailService.sendHtmlEmail(
                    new String[]{nutriologo.getCorreo()},
                    "Nueva solicitud de cita - NutriGestor",
                    contenidoHtml
            );
            log.info("Correo de solicitud de cita enviado al nutriólogo {}", nutriologo.getCorreo());
        } catch (Exception e) {
            log.warn("No se pudo enviar correo de solicitud de cita a {}: {}", nutriologo.getCorreo(), e.getMessage());
        }
    }

    private String construirNombreCompleto(String nombre, String apellidoPaterno, String apellidoMaterno) {
        StringBuilder sb = new StringBuilder();
        if (nombre != null && !nombre.isBlank()) sb.append(nombre.trim());
        if (apellidoPaterno != null && !apellidoPaterno.isBlank()) sb.append(" ").append(apellidoPaterno.trim());
        if (apellidoMaterno != null && !apellidoMaterno.isBlank()) sb.append(" ").append(apellidoMaterno.trim());
        String res = sb.toString().trim();
        return res.isEmpty() ? "Usuario" : res;
    }

    private String formatearMotivoCita(MotivoCita motivo) {
        if (motivo == null) return "Consulta general";
        return switch (motivo) {
            case PRIMERA_VEZ -> "Primera vez";
            case SEGUIMIENTO -> "Seguimiento";
            case REVISION_DE_PLAN -> "Revisión de plan";
            case NUEVO_SINTOMA -> "Nuevo síntoma";
            case CONTROL_MENSUAL -> "Control mensual";
            case OTRO -> "Otro";
        };
    }
}
