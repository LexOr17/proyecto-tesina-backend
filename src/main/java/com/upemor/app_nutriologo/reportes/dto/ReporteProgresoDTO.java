package com.upemor.app_nutriologo.reportes.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ReporteProgresoDTO {
    private List<LocalDate> fechas;
    private List<BigDecimal> pesos;
    private List<BigDecimal> porcentajesGrasa;
    private List<BigDecimal> indicesCinturaTalla;
    private List<BigDecimal> indicesCinturaCadera;
}
