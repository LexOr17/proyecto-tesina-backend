package com.upemor.app_nutriologo.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlimentoFrecuenteDTO {
    private Long alimentoId;
    private String nombre;
    private String categoria;
    private long vecesUtilizado;
    private double porcentajeUso;
}
