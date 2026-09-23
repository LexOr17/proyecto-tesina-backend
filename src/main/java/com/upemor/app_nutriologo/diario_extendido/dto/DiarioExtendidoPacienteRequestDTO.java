package com.upemor.app_nutriologo.diario_extendido.dto;

import com.upemor.app_nutriologo.diario_extendido.enums.CalidadSueno;
import com.upemor.app_nutriologo.diario_extendido.enums.EstadoAnimo;
import com.upemor.app_nutriologo.diario_extendido.enums.Sintoma;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.List;

public record DiarioExtendidoPacienteRequestDTO(

        @NotNull(message = "La fecha es obligatoria")
        @PastOrPresent(message = "No puedes registrar diarios de fechas futuras")
        LocalDate fecha,

        @NotNull(message = "El estado de ánimo es obligatorio")
        EstadoAnimo estadoAnimo,

        @NotNull(message = "La calidad del sueño es obligatoria")
        CalidadSueno calidadSueno,

        @NotNull(message = "El síntoma es obligatorio")
        Sintoma sintomaPrincipal,

        String sintomaDetalle,

        Boolean actividadRealizada,

        @Valid
        List<BebidaConsumidaDTO> bebidas
) {}