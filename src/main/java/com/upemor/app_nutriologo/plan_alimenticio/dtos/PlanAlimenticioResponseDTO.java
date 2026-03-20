package com.upemor.app_nutriologo.plan_alimenticio.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PlanAlimenticioResponseDTO {
    private Long id;
    private String nombre;
    private LocalDate fechaInicio;
    private Integer duracionDias;
    private LocalDate fechaCreacion;
    private Long pacienteId;
    private Long nutriologoId;
    private List<ComidaProgramadaResponseDTO> comidasProgramadas;
}