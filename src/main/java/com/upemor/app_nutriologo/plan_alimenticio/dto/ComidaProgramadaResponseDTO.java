package com.upemor.app_nutriologo.plan_alimenticio.dto;

import com.upemor.app_nutriologo.plantillas.enums.TipoComida;

/**
 * DTO inmutable (record) de respuesta que representa los detalles de una comida programada en un plan.
 *
 * @param id identificador único de la comida programada.
 * @param plantillaId identificador primario de la plantilla de alimentos asociada.
 * @param plantillaNombre nombre o título de la plantilla alimenticia.
 * @param diaPlan número de día secuencial asignado a la comida.
 * @param horario tiempo de comida programado (DESAYUNO, COMIDA, CENA, etc.).
 * @param observaciones indicaciones particulares para esta toma.
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public record ComidaProgramadaResponseDTO(
        Long id,
        Long plantillaId,
        String plantillaNombre,
        Integer diaPlan,
        TipoComida horario,
        String observaciones
) {}