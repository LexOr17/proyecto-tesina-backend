package com.upemor.app_nutriologo.diario_comidas.service;

import com.upemor.app_nutriologo.common.exceptions.BadRequestException;
import com.upemor.app_nutriologo.common.exceptions.DuplicateResourceException;
import com.upemor.app_nutriologo.common.exceptions.InvalidOperationException;
import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaRequestDTO;
import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaResponseDTO;
import com.upemor.app_nutriologo.diario_comidas.dtos.ObservacionRequestDTO;
import com.upemor.app_nutriologo.diario_comidas.entity.DiarioComida;
import com.upemor.app_nutriologo.diario_comidas.mapper.DiarioComidaMapper;
import com.upemor.app_nutriologo.diario_comidas.repository.DiarioComidaRepository;
import com.upemor.app_nutriologo.plan_alimenticio.entity.ComidaProgramada;
import com.upemor.app_nutriologo.plan_alimenticio.repository.ComidaProgramadaRepository;
import com.upemor.app_nutriologo.security.UserContextService;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiarioComidaServiceImpl implements DiarioComidaService {

    private final DiarioComidaRepository diarioRepository;
    private final ComidaProgramadaRepository comidaProgramadaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DiarioComidaMapper mapper;
    private final UserContextService userContextService;

    @Value("${app.diario.max-dias-gracia:0}")
    private int maxDiasGracia;

    @Value("${app.diario.horas-gracia-extra:12}")
    private int horasGraciaExtra;

    @Override
    @Transactional
    public DiarioComidaResponseDTO registrarComida(DiarioComidaRequestDTO dto) {
        Long pacienteId = userContextService.getCurrentUserId();
        validarVentanaRegistro(dto.fecha());

        diarioRepository.findByPacienteIdAndFechaAndHorario(pacienteId, dto.fecha(), dto.horario())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Ya existe un registro para " + dto.horario() + " en esta fecha.");
                });

        DiarioComida nuevo = mapper.toEntity(dto);
        nuevo.setPaciente(usuarioRepository.getReferenceById(pacienteId));

        if (dto.comidaProgramadaId() != null) {
            ComidaProgramada cp = comidaProgramadaRepository.findById(dto.comidaProgramadaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comida programada no encontrada."));
            nuevo.setComidaProgramada(cp);
        }

        return mapper.toResponseDto(diarioRepository.save(nuevo));
    }

    @Override
    @Transactional
    public DiarioComidaResponseDTO actualizarComida(Long id, DiarioComidaRequestDTO dto) {
        Long pacienteId = userContextService.getCurrentUserId();
        DiarioComida registro = diarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));

        if (!registro.getPaciente().getId().equals(pacienteId)) {
            throw new AccessDeniedException("No tienes permiso para editar este registro.");
        }

        validarVentanaRegistro(registro.getFecha());

        registro.setEstado(dto.estado());
        registro.setDetallesComidaReal(dto.detallesComidaReal());
        registro.setHorario(dto.horario());

        return mapper.toResponseDto(diarioRepository.save(registro));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiarioComidaResponseDTO> obtenerHistorialPorPaciente(Long pacienteId) {
        return mapper.toResponseDtoList(diarioRepository.findByPacienteIdOrderByFechaDesc(pacienteId));
    }

    @Override
    @Transactional
    public DiarioComidaResponseDTO agregarObservacion(Long id, ObservacionRequestDTO dto) {
        DiarioComida registro = diarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));

        registro.setObservacionNutriologo(dto.observacion());
        return mapper.toResponseDto(diarioRepository.save(registro));
    }

    @Override
    @Transactional
    public void eliminarRegistro(Long id) {
        if (!diarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("El registro no existe.");
        }
        diarioRepository.deleteById(id);
    }

    private void validarVentanaRegistro(LocalDate fechaComida) {
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaComida.isAfter(ahora.toLocalDate())) {
            throw new BadRequestException("No puedes registrar comidas futuras.");
        }

        // Lógica de ventana: Fin del día + días de gracia + horas extra
        LocalDateTime limite = fechaComida.atTime(LocalTime.MAX)
                .plusDays(maxDiasGracia).plusHours(horasGraciaExtra);

        if (ahora.isAfter(limite)) {
            throw new InvalidOperationException("El plazo para esta acción ha vencido.");
        }
    }
}