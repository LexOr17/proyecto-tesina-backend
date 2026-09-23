package com.upemor.app_nutriologo.citas.dto;

import com.upemor.app_nutriologo.citas.enums.MotivoCita;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaSolicitudDTO {

    @NotNull(message = "La fecha y hora de inicio son obligatorias")
    @Future(message = "La cita debe ser programada para una fecha futura")
    private LocalDateTime fechaHoraInicio;

    @NotNull(message = "Debes seleccionar un motivo para la cita")
    private MotivoCita motivoCita;

    @Size(max = 500, message = "El detalle no puede exceder los 500 caracteres")
    private String detalleAdicional;
}
