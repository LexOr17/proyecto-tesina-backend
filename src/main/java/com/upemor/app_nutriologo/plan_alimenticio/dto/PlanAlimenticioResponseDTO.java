package com.upemor.app_nutriologo.plan_alimenticio.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO inmutable (record) de respuesta que expone la información detallada de un plan alimenticio.
 *
 * @param id identificador único del plan alimenticio.
 * @param nombre título o denominación del plan.
 * @param fechaInicio fecha programada para el inicio del régimen.
 * @param duracionDias duración en días del plan.
 * @param fechaCreacion fecha de registro y alta en el sistema.
 * @param pacienteId identificador primario del paciente titular.
 * @param nutriologoId identificador primario del nutriólogo prescriptor.
 * @param comidasProgramadas listado de comidas programadas asignadas al plan.
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
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