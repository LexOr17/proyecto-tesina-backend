package com.upemor.app_nutriologo.diario_comidas.dtos;

import com.upemor.app_nutriologo.diario_comidas.enums.EstadoRegistro;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO para el registro o actualización del consumo de una comida en el diario del paciente.
 *
 * @param comidaProgramadaId Identificador opcional de la comida programada en el plan alimenticio.
 * @param fecha              Fecha en la que se realizó o debió realizarse el consumo.
 * @param horario            Momento o tipo de comida correspondiente (desayuno, almuerzo, merienda, cena).
 * @param estado             Estado de cumplimiento del consumo (cumplido, modificado, saltada, etc.).
 * @param detallesComidaReal Descripción de los alimentos y porciones efectivamente consumidos.
 * @author Sistema Nutriólogo
 * @version 1.0
 */
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

