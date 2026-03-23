package com.upemor.app_nutriologo.diario_comidas.dtos;

import com.upemor.app_nutriologo.diario_comidas.enums.EstadoRegistro;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DiarioComidaRequestDTO(

        Long comidaProgramadaId,

        @NotNull(message = "La fecha del consumo es obligatoria")
        LocalDate fecha,

        @NotNull(message = "El horario es obligatorio")
        TipoComida horario,

        @NotNull(message = "El estado del registro es obligatorio")
        EstadoRegistro estado,

        @Size(max = 255, message = "El detalle de la comida no puede exceder los 255 caracteres")
        String detallesComidaReal
) {
}

