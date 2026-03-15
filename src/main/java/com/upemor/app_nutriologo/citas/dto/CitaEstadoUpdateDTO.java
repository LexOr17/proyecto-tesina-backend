package com.upemor.app_nutriologo.citas.dto;

import com.upemor.app_nutriologo.citas.enums.Estado;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaEstadoUpdateDTO {

    private Estado nuevoEstado;
    private String motivo;
    private LocalDateTime nuevaFechaHoraInicio;
    private String observaciones;

}
