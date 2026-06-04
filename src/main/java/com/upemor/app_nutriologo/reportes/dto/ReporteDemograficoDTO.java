package com.upemor.app_nutriologo.reportes.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ReporteDemograficoDTO {
    private long totalPacientes;
    private Map<String, Long> distribucionPorNutriologo;
    private Map<String, Long> distribucionPorGenero;
    private Map<String, Long> distribucionPorRangoEdad;
}
