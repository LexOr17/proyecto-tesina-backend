package com.upemor.app_nutriologo.citas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaReprogramarDTO {

    @NotNull(message = "La nueva fecha y hora de inicio de la cita es obligatoria.")
    @Future(message = "La nueva fecha y hora de la cita debe ser en el futuro.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime nuevaFechaHoraInicio;

}
