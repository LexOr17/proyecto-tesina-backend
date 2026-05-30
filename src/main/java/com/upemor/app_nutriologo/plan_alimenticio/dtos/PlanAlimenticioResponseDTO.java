package com.upemor.app_nutriologo.plan_alimenticio.dtos;

import java.time.LocalDate;
import java.util.List;

public record PlanAlimenticioResponseDTO(
                Long id,
                String nombre,
                LocalDate fechaInicio,
                Integer duracionDias,
                LocalDate fechaCreacion,
                Long pacienteId,
                Long nutriologoId,
                List<ComidaProgramadaResponseDTO> comidasProgramadas) {
}