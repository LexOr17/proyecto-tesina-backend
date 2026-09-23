package com.upemor.app_nutriologo.diario_comidas.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para el registro o actualización de una observación profesional emitida por el nutriólogo
 * sobre una comida registrada por un paciente.
 *
 * @param observacion Texto con la retroalimentación, recomendaciones o nota clínica del nutriólogo.
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public record ObservacionRequestDTO(
        @NotBlank(message = "La observación no puede estar vacía")
        @Size(max = 500, message = "La observación no debe exceder los 500 caracteres")
        String observacion
) {}
