package com.upemor.app_nutriologo.calendario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Respuesta del endpoint GET /calendario/paciente/mes.
 * Contiene los indicadores de cada día del mes solicitado para poblar el grid.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarioMesPacienteResponse {

    private int year;
    private int month;
    private List<DiaResumenPacienteDTO> dias;
}
