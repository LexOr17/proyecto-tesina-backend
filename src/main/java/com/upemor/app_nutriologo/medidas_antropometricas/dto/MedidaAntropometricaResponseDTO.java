package com.upemor.app_nutriologo.medidas_antropometricas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedidaAntropometricaResponseDTO {

    private Long id;
    private BigDecimal peso;
    private BigDecimal altura;
    private BigDecimal cintura;
    private BigDecimal cadera;
    private BigDecimal cuello;
    private LocalDate fechaMedicion;
}
