package com.upemor.app_nutriologo.diario_comidas.dtos;

import com.upemor.app_nutriologo.diario_comidas.enums.EstadoRegistro;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta que detalla un registro de comida en el diario de un paciente.
 * <p>
 * Contiene tanto los datos de la comida consumida como la información de referencia
 * del plan alimenticio y las observaciones clínicas emitidas por el nutriólogo.
 * </p>
 *
 * @param id                    Identificador único del registro de diario.
 * @param comidaProgramadaId    Identificador de la comida programada asociada (si aplica).
 * @param nombreComidaSugerida  Nombre o título de la plantilla sugerida en el plan.
 * @param fecha                 Fecha en que se registró el consumo.
 * @param horario               Momento o tipo de comida (desayuno, almuerzo, merienda, cena).
 * @param estado                Estado de cumplimiento reportado.
 * @param detallesComidaReal    Detalles o descripción de lo consumido realmente.
 * @param observacionNutriologo Retroalimentación u observación añadida por el nutriólogo.
 * @param fechaRegistroSistema  Fecha y hora exacta en la que se persistió el registro.
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public record DiarioComidaResponseDTO(
        Long id,
        Long comidaProgramadaId,
        String nombreComidaSugerida,
        LocalDate fecha,
        TipoComida horario,
        EstadoRegistro estado,
        String detallesComidaReal,
        String observacionNutriologo,
        LocalDateTime fechaRegistroSistema
) {}
