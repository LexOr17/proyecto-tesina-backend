package com.upemor.app_nutriologo.diario_extendido.service;

import com.upemor.app_nutriologo.common.exceptions.BadRequestException;
import com.upemor.app_nutriologo.common.exceptions.InvalidOperationException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoNutriologoRequestDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoPacienteRequestDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoResponseDTO;
import com.upemor.app_nutriologo.diario_extendido.entity.BebidaConsumida;
import com.upemor.app_nutriologo.diario_extendido.entity.DiarioExtendido;
import com.upemor.app_nutriologo.diario_extendido.enums.Sintoma;
import com.upemor.app_nutriologo.diario_extendido.mapper.DiarioExtendidoMapper;
import com.upemor.app_nutriologo.diario_extendido.repository.DiarioExtendidoRepository;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import com.upemor.app_nutriologo.security.UserContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DiarioExtendidoServiceImpl implements DiarioExtendidoService {

    private final DiarioExtendidoRepository diarioRepository;
    private final PacienteRepository pacienteRepository;
    private final DiarioExtendidoMapper diarioMapper;
    private final UserContextService userContextService;

    // Configuración de tolerancia (Días de gracia y horas extra)
    @Value("${app.diario.max-dias-gracia:0}")
    private int maxDiasGracia;

    @Value("${app.diario.horas-gracia-extra:12}")
    private int horasGraciaExtra;

    @Override
    @Transactional
    public DiarioExtendidoResponseDTO registrarOActualizarDiario(DiarioExtendidoPacienteRequestDTO dto) {
        Long pacienteId = userContextService.getCurrentUserId();

        // 1. VALIDACIÓN DE TIEMPO: ¿Está dentro de las 12h de gracia?
        validarVentanaRegistro(dto.fecha());

        // 2. VALIDACIÓN DE NEGOCIO: Sintoma detalle obligatorio si es 'OTRO'
        if (dto.sintomaPrincipal() == Sintoma.OTRO && (dto.sintomaDetalle() == null || dto.sintomaDetalle().isBlank())) {
            throw new BadRequestException("Debes especificar el síntoma detallado al seleccionar 'OTRO'.");
        }

        // 3. OBTENER O CREAR: Buscamos el registro de ese día (puede existir por el nutriólogo)
        DiarioExtendido diario = obtenerOAsegurarDiario(pacienteId, dto.fecha());

        // 4. MAPEO: El mapper ya ignora campos del nutriólogo por configuración previa
        diarioMapper.updateEntityFromDto(dto, diario);

        // 5. SINCRONIZACIÓN DE BEBIDAS: Limpia y agrega (Permite corregir si olvidó alguna)
        actualizarBebidas(diario, dto);

        return diarioMapper.toResponseDto(diarioRepository.save(diario));
    }

    @Override
    @Transactional
    public DiarioExtendidoResponseDTO sugerirActividad(Long pacienteId, LocalDate fecha, DiarioExtendidoNutriologoRequestDTO dto) {
        Long nutriologoId = userContextService.getCurrentUserId();

        // El nutriólogo no tiene restricción de tiempo (puede planear el futuro)
        DiarioExtendido diario = obtenerOAsegurarDiario(pacienteId, fecha);
        validarRelacionPacienteNutriologo(diario.getPaciente(), nutriologoId);

        diarioMapper.updateEntityFromNutriologoDto(dto, diario);

        return diarioMapper.toResponseDto(diarioRepository.save(diario));
    }

    @Override
    @Transactional(readOnly = true)
    public DiarioExtendidoResponseDTO buscarDiarioPorFecha(Long pacienteId, LocalDate fecha) {
        Long idFinal = (pacienteId == null) ? userContextService.getCurrentUserId() : pacienteId;
        validarAccesoAPaciente(idFinal);

        return diarioRepository.findByPacienteIdAndFecha(idFinal, fecha)
                .map(diarioMapper::toResponseDto)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DiarioExtendidoResponseDTO> listarDiariosDePaciente(Long pacienteId, Pageable pageable) {
        validarAccesoAPaciente(pacienteId);
        return diarioRepository.findByPacienteIdOrderByFechaDesc(pacienteId, pageable)
                .map(diarioMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void eliminarDiario(Long diarioId) {
        DiarioExtendido diario = diarioRepository.findById(diarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Diario no encontrado."));
        diarioRepository.delete(diario);
    }

    // --- MÉTODOS PRIVADOS DE APOYO ---

    private void validarVentanaRegistro(LocalDate fechaRegistro) {
        LocalDateTime ahora = LocalDateTime.now();

        // No permitir fechas futuras
        if (fechaRegistro.isAfter(ahora.toLocalDate())) {
            throw new BadRequestException("No puedes registrar un diario para una fecha futura.");
        }

        // Cálculo del límite: Fin del día + Gracia
        LocalDateTime limite = fechaRegistro.atTime(LocalTime.MAX)
                .plusDays(maxDiasGracia)
                .plusHours(horasGraciaExtra);

        if (ahora.isAfter(limite)) {
            throw new InvalidOperationException("El plazo para registrar o modificar el diario del " + fechaRegistro + " ha vencido.");
        }
    }

    private DiarioExtendido obtenerOAsegurarDiario(Long pacienteId, LocalDate fecha) {
        return diarioRepository.findByPacienteIdAndFecha(pacienteId, fecha)
                .orElseGet(() -> {
                    Paciente paciente = pacienteRepository.findById(pacienteId)
                            .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado."));

                    return DiarioExtendido.builder()
                            .paciente(paciente)
                            .fecha(fecha)
                            .actividadRealizada(false)
                            .build();
                });
    }

    private void actualizarBebidas(DiarioExtendido diario, DiarioExtendidoPacienteRequestDTO dto) {
        diario.getBebidas().clear(); // Hibernate maneja el borrado de huérfanos
        if (dto.bebidas() != null) {
            dto.bebidas().forEach(bebidaDto -> {
                BebidaConsumida nuevaBebida = diarioMapper.toEntity(bebidaDto);
                diario.addBebida(nuevaBebida);
            });
        }
    }

    private void validarAccesoAPaciente(Long pacienteId) {
        Long usuarioAutenticadoId = userContextService.getCurrentUserId();
        if (!usuarioAutenticadoId.equals(pacienteId)) {
            Paciente paciente = pacienteRepository.findById(pacienteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado."));
            validarRelacionPacienteNutriologo(paciente, usuarioAutenticadoId);
        }
    }

    private void validarRelacionPacienteNutriologo(Paciente paciente, Long nutriologoId) {
        if (paciente.getNutriologo() == null || !paciente.getNutriologo().getId().equals(nutriologoId)) {
            throw new AccessDeniedException("No tienes permiso para acceder a los datos de este paciente.");
        }
    }
}