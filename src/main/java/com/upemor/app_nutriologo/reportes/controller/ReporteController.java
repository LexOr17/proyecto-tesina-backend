package com.upemor.app_nutriologo.reportes.controller;

import com.upemor.app_nutriologo.reportes.dto.ReporteAsistenciaDTO;
import com.upemor.app_nutriologo.reportes.dto.ReporteDemograficoDTO;
import com.upemor.app_nutriologo.reportes.dto.ReporteDesempenoNutriologoDTO;
import com.upemor.app_nutriologo.reportes.dto.ReporteProgresoDTO;
import com.upemor.app_nutriologo.reportes.dto.ReportePropuestaPlanDTO;
import com.upemor.app_nutriologo.reportes.dto.ReporteSeguimientoPlanDTO;
import com.upemor.app_nutriologo.reportes.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/demografico")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ReporteDemograficoDTO> obtenerReporteDemografico() {
        return ResponseEntity.ok(reporteService.generarReporteDemografico());
    }

    @GetMapping("/progreso/{pacienteId}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR', 'PACIENTE')")
    public ResponseEntity<ReporteProgresoDTO> obtenerReporteProgreso(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(reporteService.generarReporteProgreso(pacienteId));
    }

    @GetMapping("/seguimiento-plan/{pacienteId}")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR', 'PACIENTE')")
    public ResponseEntity<ReporteSeguimientoPlanDTO> obtenerReporteSeguimientoPlan(
            @PathVariable Long pacienteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(reporteService.generarReporteSeguimientoPlan(pacienteId, inicio, fin));
    }

    /**
     * Reporte de Asistencia y Demanda de Servicios.
     */
    @GetMapping("/asistencia")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')")
    public ResponseEntity<ReporteAsistenciaDTO> obtenerReporteAsistencia(
            @RequestParam(required = false) Long nutriologoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(reporteService.generarReporteAsistencia(nutriologoId, inicio, fin));
    }

    /**
     * Reporte de Desempeño y Atención del Nutriólogo.
     */
    @GetMapping("/desempeno-nutriologo")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')")
    public ResponseEntity<ReporteDesempenoNutriologoDTO> obtenerReporteDesempenoNutriologo(
            @RequestParam(required = false) Long nutriologoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(reporteService.generarReporteDesempenoNutriologo(nutriologoId, inicio, fin));
    }

    /**
     * Reporte de Propuesta de Plan para Pacientes (Receptividad y Comunicación Proactiva).
     *
     * @param nutriologoId (opcional) ID del nutriólogo a filtrar (solo para ADMINISTRADOR; null = global).
     * @param inicio       (opcional) Fecha inicio del rango (ISO). Default: últimos 30 días.
     * @param fin          (opcional) Fecha fin del rango (ISO). Default: hoy.
     */
    @GetMapping("/propuesta-plan")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')")
    public ResponseEntity<ReportePropuestaPlanDTO> obtenerReportePropuestaPlan(
            @RequestParam(required = false) Long nutriologoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(reporteService.generarReportePropuestaPlan(nutriologoId, inicio, fin));
    }
}
