package com.upemor.app_nutriologo.plan_alimenticio.dtos;

import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComidaProgramadaDTO {

    @NotNull(message = "El ID de la plantilla es obligatorio")
    private Long plantillaId;

    @NotNull(message = "El día del plan es obligatorio")
    @Min(value = 1, message = "El día del plan debe ser al menos 1")
    private Integer diaPlan;

    @NotNull(message = "El horario (Desayuno, Comida, etc.) es obligatorio")
    private TipoComida horario;

    // Las observaciones son opcionales, así que no llevan @NotNull
    private String observaciones;
}
