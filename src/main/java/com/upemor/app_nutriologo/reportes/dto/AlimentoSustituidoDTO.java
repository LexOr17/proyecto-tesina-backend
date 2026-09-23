package com.upemor.app_nutriologo.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlimentoSustituidoDTO {
    private Long alimentoId;
    private String nombre;
    private String categoria;
    private long vecesSolicitado;
    private double porcentajeTotal;
}
