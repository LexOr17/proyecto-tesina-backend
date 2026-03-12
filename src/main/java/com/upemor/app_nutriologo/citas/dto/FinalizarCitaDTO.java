package com.upemor.app_nutriologo.citas.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FinalizarCitaDTO {

    @NotNull(message = "El campo 'asistio' no puede ser nulo")
    private Boolean asistio;

    private String observaciones;

}
