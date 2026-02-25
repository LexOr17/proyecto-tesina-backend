package com.upemor.app_nutriologo.medidas_antropometricas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedidaAntropometricaRequestDTO {

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.1", message = "El peso debe ser mayor a 0")
    private BigDecimal peso;

    @NotNull(message = "La altura es obligatoria")
    @DecimalMin(value = "0.1", message = "La altura debe ser mayor a 0")
    private BigDecimal altura;

    private BigDecimal cintura;
    private BigDecimal cadera;
    private BigDecimal cuello;
}
