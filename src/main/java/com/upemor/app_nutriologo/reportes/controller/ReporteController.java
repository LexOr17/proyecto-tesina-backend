package com.upemor.app_nutriologo.reportes.controller;

import com.upemor.app_nutriologo.reportes.dto.ReporteDemograficoDTO;
import com.upemor.app_nutriologo.reportes.dto.ReporteProgresoDTO;
import com.upemor.app_nutriologo.reportes.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/demografico")
    public ResponseEntity<ReporteDemograficoDTO> obtenerReporteDemografico() {
        return ResponseEntity.ok(reporteService.generarReporteDemografico());
    }

    @GetMapping("/progreso/{pacienteId}")
    public ResponseEntity<ReporteProgresoDTO> obtenerReporteProgreso(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(reporteService.generarReporteProgreso(pacienteId));
    }
}
