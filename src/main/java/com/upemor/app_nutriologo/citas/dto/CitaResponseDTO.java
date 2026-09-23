package com.upemor.app_nutriologo.citas.dto;

import com.upemor.app_nutriologo.citas.enums.Estado;
import com.upemor.app_nutriologo.citas.enums.MotivoCita;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaResponseDTO {
    private Long id;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private Estado estado;
    private MotivoCita motivoCita;
    private String detalleAdicional;
    private String observaciones;
    private LocalDateTime fechaHoraReprogramacionSolicitada;
    private Long pacienteId;
    private String nombrePaciente;
    private Long nutriologoId;
    private String nombreNutriologo;
}
