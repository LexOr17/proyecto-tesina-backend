package com.upemor.app_nutriologo.calendario.service;

import com.upemor.app_nutriologo.calendario.dto.*;
import com.upemor.app_nutriologo.citas.entity.Cita;
import com.upemor.app_nutriologo.citas.enums.Estado;
import com.upemor.app_nutriologo.citas.repository.CitaRepository;
import com.upemor.app_nutriologo.diario_comidas.entity.DiarioComida;
import com.upemor.app_nutriologo.diario_comidas.repository.DiarioComidaRepository;
import com.upemor.app_nutriologo.diario_extendido.entity.DiarioExtendido;
import com.upemor.app_nutriologo.diario_extendido.repository.DiarioExtendidoRepository;
import com.upemor.app_nutriologo.plan_alimenticio.entity.PlanAlimenticio;
import com.upemor.app_nutriologo.plan_alimenticio.repository.PlanAlimenticioRepository;
import com.upemor.app_nutriologo.security.UserContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarioServiceImpl implements CalendarioService {

        // ─── Configuración de horario laboral ─────────────────────────────────────
        private static final LocalTime HORA_INICIO_JORNADA = LocalTime.of(9, 0);
        private static final LocalTime HORA_FIN_JORNADA = LocalTime.of(18, 0);
        private static final int DURACION_BLOQUE_HORAS = 1;

        private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

        private final CitaRepository citaRepository;
        private final PlanAlimenticioRepository planAlimenticioRepository;
        private final DiarioComidaRepository diarioComidaRepository;
        private final DiarioExtendidoRepository diarioExtendidoRepository;
        private final UserContextService userContextService;

        // ─────────────────────────────────────────────────────────────────────────
        // NUTRIÓLOGO
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        public CalendarioMesNutriologoResponse getMesNutriologo(int year, int month) {
                Long nutriologoId = userContextService.getCurrentUserId();
                YearMonth ym = YearMonth.of(year, month);

                // Cargamos todas las citas del mes en una sola consulta
                LocalDateTime inicioMes = ym.atDay(1).atStartOfDay();
                LocalDateTime finMes = ym.atEndOfMonth().atTime(LocalTime.MAX);
                List<Cita> citasMes = citaRepository.findCitasByNutriologoIdAndRangoFechas(
                                nutriologoId, inicioMes, finMes);

                // Agrupamos citas por fecha para procesamiento eficiente
                Map<LocalDate, List<Cita>> citasPorFecha = citasMes.stream()
                                .collect(Collectors.groupingBy(c -> c.getFechaHoraInicio().toLocalDate()));

                // Calculamos pacientes con plan activo y sin diario para el MES completo
                // (lo hacemos de forma lazy por día para no cargar demasiado)
                List<DiaResumenNutriologoDTO> dias = new ArrayList<>();
                for (int dia = 1; dia <= ym.lengthOfMonth(); dia++) {
                        LocalDate fecha = ym.atDay(dia);
                        List<Cita> citasDelDia = citasPorFecha.getOrDefault(fecha, List.of());

                        // Solo calcular pacientes sin diario para días pasados y hoy
                        int sinDiario = 0;
                        if (!fecha.isAfter(LocalDate.now())) {
                                sinDiario = contarPacientesSinDiario(nutriologoId, fecha, citasDelDia);
                        }

                        boolean tieneHorasLibres = !calcularHorasLibres(fecha, citasDelDia).isEmpty();

                        dias.add(DiaResumenNutriologoDTO.builder()
                                        .fecha(fecha.format(DATE_FORMATTER))
                                        .tieneCitas(!citasDelDia.isEmpty())
                                        .cantidadCitas(citasDelDia.size())
                                        .tieneHorasLibres(tieneHorasLibres)
                                        .pacientesSinDiario(sinDiario)
                                        .build());
                }

                return CalendarioMesNutriologoResponse.builder()
                                .year(year)
                                .month(month)
                                .dias(dias)
                                .build();
        }

        @Override
        public CalendarioDiaNutriologoResponse getDiaNutriologo(LocalDate fecha) {
                Long nutriologoId = userContextService.getCurrentUserId();

                // 1. Citas del día
                LocalDateTime inicioDia = fecha.atStartOfDay();
                LocalDateTime finDia = fecha.atTime(LocalTime.MAX);
                List<Cita> citas = citaRepository.findCitasByNutriologoIdAndRangoFechas(
                                nutriologoId, inicioDia, finDia);

                List<CitaCalendarioDTO> citasDTOs = citas.stream()
                                .map(this::toCitaNutriologoDTO)
                                .collect(Collectors.toList());

                // 2. Horas libres
                List<HoraLibreDTO> horasLibres = calcularHorasLibres(fecha, citas);

                // 3. Pacientes sin diario
                List<PacienteSinDiarioDTO> sinDiario = obtenerPacientesSinDiarioDetalle(
                                nutriologoId, fecha, citas);

                return CalendarioDiaNutriologoResponse.builder()
                                .fecha(fecha.format(DATE_FORMATTER))
                                .citas(citasDTOs)
                                .horasLibres(horasLibres)
                                .pacientesSinDiario(sinDiario)
                                .build();
        }

        // ─────────────────────────────────────────────────────────────────────────
        // PACIENTE
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        public CalendarioMesPacienteResponse getMesPaciente(int year, int month) {
                Long pacienteId = userContextService.getCurrentUserId();
                YearMonth ym = YearMonth.of(year, month);

                // 1. Citas del mes
                LocalDateTime inicioMes = ym.atDay(1).atStartOfDay();
                LocalDateTime finMes = ym.atEndOfMonth().atTime(LocalTime.MAX);
                List<Cita> citasMes = citaRepository.findCitasByPacienteIdAndRangoFechas(
                                pacienteId, inicioMes, finMes);
                Set<LocalDate> diasConCita = citasMes.stream()
                                .map(c -> c.getFechaHoraInicio().toLocalDate())
                                .collect(Collectors.toSet());

                // 2. Diarios registrados del mes
                List<DiarioComida> diariosMes = diarioComidaRepository
                                .findByPacienteIdAndFechaBetween(
                                                pacienteId,
                                                ym.atDay(1),
                                                ym.atEndOfMonth());
                Map<LocalDate, List<DiarioComida>> diariosPorFecha = diariosMes.stream()
                                .collect(Collectors.groupingBy(DiarioComida::getFecha));

                // 3. Diarios de bienestar del mes
                List<DiarioExtendido> bienestarMes = diarioExtendidoRepository
                                .findByPacienteIdAndFechaBetweenOrderByFechaAsc(
                                                pacienteId,
                                                ym.atDay(1),
                                                ym.atEndOfMonth());
                Set<LocalDate> diasConBienestar = bienestarMes.stream()
                                .map(DiarioExtendido::getFecha)
                                .collect(Collectors.toSet());

                // 4. Plan activo (si existe)
                List<PlanAlimenticio> planesActivos = planAlimenticioRepository
                                .findPlanesActivosByPacienteId(pacienteId, LocalDate.now());
                PlanAlimenticio planActivo = planesActivos.isEmpty() ? null : planesActivos.get(0);

                List<DiaResumenPacienteDTO> dias = new ArrayList<>();
                for (int d = 1; d <= ym.lengthOfMonth(); d++) {
                        LocalDate fecha = ym.atDay(d);

                        boolean tienePlan = false;
                        boolean diarioCompleto = false;

                        if (planActivo != null) {
                                // Calcular si la fecha cae dentro del rango del plan
                                LocalDate inicioP = planActivo.getFechaInicio();
                                LocalDate finP = inicioP.plusDays(planActivo.getDuracionDias() - 1);
                                tienePlan = !fecha.isBefore(inicioP) && !fecha.isAfter(finP);

                                if (tienePlan) {
                                        int diaPlan = (int) (fecha.toEpochDay() - inicioP.toEpochDay()) + 1;
                                        long comidasProgramadasCount = planActivo.getComidasProgramadas().stream()
                                                        .filter(cp -> cp.getDiaPlan().equals(diaPlan))
                                                        .count();

                                        List<DiarioComida> diariosDelDia = diariosPorFecha.getOrDefault(fecha,
                                                        List.of());
                                        diarioCompleto = comidasProgramadasCount > 0 &&
                                                        diariosDelDia.size() >= comidasProgramadasCount;
                                }
                        }

                        dias.add(DiaResumenPacienteDTO.builder()
                                        .fecha(fecha.format(DATE_FORMATTER))
                                        .tieneCita(diasConCita.contains(fecha))
                                        .tienePlan(tienePlan)
                                        .diarioCompleto(diarioCompleto)
                                        .bienestarRegistrado(diasConBienestar.contains(fecha))
                                        .build());
                }

                return CalendarioMesPacienteResponse.builder()
                                .year(year)
                                .month(month)
                                .dias(dias)
                                .build();
        }

        @Override
        public CalendarioDiaPacienteResponse getDiaPaciente(LocalDate fecha) {
                Long pacienteId = userContextService.getCurrentUserId();

                // 1. Citas del día
                LocalDateTime inicioDia = fecha.atStartOfDay();
                LocalDateTime finDia = fecha.atTime(LocalTime.MAX);
                List<Cita> citas = citaRepository.findCitasByPacienteIdAndRangoFechas(
                                pacienteId, inicioDia, finDia);
                List<CitaCalendarioDTO> citasDTOs = citas.stream()
                                .map(this::toCitaPacienteDTO)
                                .collect(Collectors.toList());

                // 2. Comidas programadas del plan activo para esta fecha
                List<ComidaProgramadaCalendarioDTO> comidasDTO = List.of();
                List<PlanAlimenticio> planesActivos = planAlimenticioRepository
                                .findPlanesActivosByPacienteId(pacienteId, fecha);

                if (!planesActivos.isEmpty()) {
                        PlanAlimenticio plan = planesActivos.get(0);
                        int diaPlan = (int) (fecha.toEpochDay() - plan.getFechaInicio().toEpochDay()) + 1;
                        comidasDTO = plan.getComidasProgramadas().stream()
                                        .filter(cp -> cp.getDiaPlan().equals(diaPlan))
                                        .sorted(Comparator.comparing(cp -> cp.getHorario().name()))
                                        .map(cp -> ComidaProgramadaCalendarioDTO.builder()
                                                        .id(cp.getId())
                                                        .horario(cp.getHorario().name())
                                                        .plantillaNombre(cp.getPlantilla().getNombre())
                                                        .observaciones(cp.getObservaciones())
                                                        .build())
                                        .collect(Collectors.toList());
                }

                // 3. Registros del diario de comida en esta fecha
                List<DiarioComida> diarios = diarioComidaRepository
                                .findByPacienteIdAndFecha(pacienteId, fecha);
                List<RegistroDiarioCalendarioDTO> diariosDTO = diarios.stream()
                                .map(d -> RegistroDiarioCalendarioDTO.builder()
                                                .id(d.getId())
                                                .horario(d.getHorario().name())
                                                .estado(d.getEstado().name())
                                                .comidaProgramadaId(d.getComidaProgramada() != null
                                                                ? d.getComidaProgramada().getId()
                                                                : null)
                                                .build())
                                .collect(Collectors.toList());

                // 4. Bienestar del día
                Optional<DiarioExtendido> bienestarOpt = diarioExtendidoRepository
                                .findByPacienteIdAndFecha(pacienteId, fecha);
                BienestarCalendarioDTO bienestarDTO = bienestarOpt
                                .map(b -> BienestarCalendarioDTO.builder()
                                                .id(b.getId())
                                                .registrado(true)
                                                .build())
                                .orElse(BienestarCalendarioDTO.builder()
                                                .registrado(false)
                                                .build());

                return CalendarioDiaPacienteResponse.builder()
                                .fecha(fecha.format(DATE_FORMATTER))
                                .citas(citasDTOs)
                                .comidasProgramadas(comidasDTO)
                                .registrosDiario(diariosDTO)
                                .bienestar(bienestarDTO)
                                .build();
        }

        // ─────────────────────────────────────────────────────────────────────────
        // HELPERS PRIVADOS
        // ─────────────────────────────────────────────────────────────────────────

        /**
         * Calcula los bloques de hora libre en el día para el nutriólogo.
         * Rango fijo: 09:00–18:00, bloques de 1 hora.
         * Se excluyen bloques que se crucen con citas no canceladas.
         */
        private List<HoraLibreDTO> calcularHorasLibres(LocalDate fecha, List<Cita> citasDelDia) {
                List<HoraLibreDTO> horasLibres = new ArrayList<>();
                LocalTime hora = HORA_INICIO_JORNADA;

                // Solo mostrar horas libres en días futuros o de hoy
                if (fecha.isBefore(LocalDate.now())) {
                        return horasLibres;
                }

                while (hora.isBefore(HORA_FIN_JORNADA)) {
                        LocalDateTime bloqueInicio = fecha.atTime(hora);
                        LocalDateTime bloqueFin = bloqueInicio.plusHours(DURACION_BLOQUE_HORAS);

                        boolean ocupado = citasDelDia.stream()
                                        .filter(c -> c.getEstado() != Estado.CANCELADA)
                                        .anyMatch(c -> bloqueInicio.isBefore(c.getFechaHoraFin())
                                                        && bloqueFin.isAfter(c.getFechaHoraInicio()));

                        if (!ocupado) {
                                horasLibres.add(HoraLibreDTO.builder()
                                                .hora(hora.toString())
                                                .fechaHoraCompleta(bloqueInicio.format(ISO_FORMATTER))
                                                .build());
                        }

                        hora = hora.plusHours(DURACION_BLOQUE_HORAS);
                }

                return horasLibres;
        }

        /**
         * Cuenta cuántos pacientes del nutriólogo tienen plan activo pero no
         * han registrado su diario en la fecha indicada.
         */
        private int contarPacientesSinDiario(Long nutriologoId, LocalDate fecha,
                        List<Cita> citasDelDia) {
                return (int) obtenerPacientesSinDiarioDetalle(nutriologoId, fecha, citasDelDia).size();
        }

        /**
         * Obtiene los pacientes que tienen plan activo en la fecha dada y que
         * no han registrado al menos un entrada de diario ese día.
         * Se limita a pacientes que tienen (o tuvieron) cita con este nutriólogo.
         */
        private List<PacienteSinDiarioDTO> obtenerPacientesSinDiarioDetalle(
                        Long nutriologoId, LocalDate fecha, List<Cita> citasDelDia) {

                // Solo verificamos pacientes con cita el mismo día (alcance razonable)
                Set<Long> pacienteIdsConCita = citasDelDia.stream()
                                .filter(c -> c.getEstado() != Estado.CANCELADA)
                                .map(c -> c.getPaciente().getId())
                                .collect(Collectors.toSet());

                if (pacienteIdsConCita.isEmpty()) {
                        return List.of();
                }

                List<PacienteSinDiarioDTO> resultado = new ArrayList<>();
                for (Long pacienteId : pacienteIdsConCita) {
                        boolean tienePlanActivo = planAlimenticioRepository
                                        .existsPlanActivo(pacienteId, fecha);
                        if (!tienePlanActivo)
                                continue;

                        List<DiarioComida> diariosDelDia = diarioComidaRepository
                                        .findByPacienteIdAndFecha(pacienteId, fecha);
                        if (diariosDelDia.isEmpty()) {
                                // Buscar el nombre del paciente desde las citas
                                citasDelDia.stream()
                                                .filter(c -> c.getPaciente().getId().equals(pacienteId))
                                                .findFirst()
                                                .ifPresent(c -> resultado.add(PacienteSinDiarioDTO.builder()
                                                                .pacienteId(pacienteId)
                                                                .nombreCompleto(construirNombreCompleto(
                                                                                c.getPaciente().getNombre(),
                                                                                c.getPaciente().getApellidoPaterno(),
                                                                                c.getPaciente().getApellidoMaterno()))
                                                                .build()));
                        }
                }

                return resultado;
        }

        private CitaCalendarioDTO toCitaNutriologoDTO(Cita cita) {
                return CitaCalendarioDTO.builder()
                                .id(cita.getId())
                                .pacienteId(cita.getPaciente().getId())
                                .pacienteNombre(construirNombreCompleto(
                                                cita.getPaciente().getNombre(),
                                                cita.getPaciente().getApellidoPaterno(),
                                                cita.getPaciente().getApellidoMaterno()))
                                .fechaHoraInicio(cita.getFechaHoraInicio().format(ISO_FORMATTER))
                                .fechaHoraFin(cita.getFechaHoraFin().format(ISO_FORMATTER))
                                .estado(cita.getEstado().name())
                                .motivoCita(cita.getMotivoCita().name())
                                .build();
        }

        private CitaCalendarioDTO toCitaPacienteDTO(Cita cita) {
                return CitaCalendarioDTO.builder()
                                .id(cita.getId())
                                .nutriologoNombre(construirNombreCompleto(
                                                cita.getNutriologo().getNombre(),
                                                cita.getNutriologo().getApellidoPaterno(),
                                                cita.getNutriologo().getApellidoMaterno()))
                                .fechaHoraInicio(cita.getFechaHoraInicio().format(ISO_FORMATTER))
                                .fechaHoraFin(cita.getFechaHoraFin().format(ISO_FORMATTER))
                                .estado(cita.getEstado().name())
                                .motivoCita(cita.getMotivoCita().name())
                                .build();
        }

        private String construirNombreCompleto(String nombre, String apellidoPaterno, String apellidoMaterno) {
                StringBuilder sb = new StringBuilder();
                if (nombre != null)
                        sb.append(nombre);
                if (apellidoPaterno != null)
                        sb.append(" ").append(apellidoPaterno);
                if (apellidoMaterno != null && !apellidoMaterno.isBlank())
                        sb.append(" ").append(apellidoMaterno);
                return sb.toString().trim();
        }
}
