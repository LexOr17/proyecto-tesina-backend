package com.upemor.app_nutriologo.citas.dto;

import com.upemor.app_nutriologo.citas.enums.MotivoCita;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaSolicitudDTO {

    @NotNull(message = "La fecha y hora de inicio no pueden ser nulas")
    @Future(message = "La fecha de inicio debe ser en el futuro")
    private LocalDateTime fechaHoraInicio;

    @NotNull(message = "El motivo principal no puede ser nulo")
    private MotivoCita motivoCita;

    @Size(max = 500, message = "El detalle adicional no puede tener más de 500 caracteres")
    private String detalleAdicional;
}
