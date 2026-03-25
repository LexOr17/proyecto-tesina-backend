package com.upemor.app_nutriologo.citas.dto;

import com.upemor.app_nutriologo.citas.enums.Estado;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaEstadoUpdateDTO {

    @NotNull(message = "El nuevo estado es obligatorio")
    private Estado nuevoEstado;

    private String motivo; // Útil para explicar una cancelación

    @Future(message = "La nueva fecha debe ser en el futuro")
    private LocalDateTime nuevaFechaHoraInicio;

    private String observaciones;
}
