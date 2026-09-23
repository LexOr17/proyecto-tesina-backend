package com.upemor.app_nutriologo.calendario.controller;

import com.upemor.app_nutriologo.calendario.dto.CalendarioDiaNutriologoResponse;
import com.upemor.app_nutriologo.calendario.dto.CalendarioDiaPacienteResponse;
import com.upemor.app_nutriologo.calendario.dto.CalendarioMesNutriologoResponse;
import com.upemor.app_nutriologo.calendario.dto.CalendarioMesPacienteResponse;
import com.upemor.app_nutriologo.calendario.service.CalendarioService;
import com.upemor.app_nutriologo.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controlador del calendario interactivo.
 *
 * Endpoints:
 *  - GET /calendario/nutriologo/mes?year=&month=
 *  - GET /calendario/nutriologo/dia?fecha=
 *  - GET /calendario/paciente/mes?year=&month=
 *  - GET /calendario/paciente/dia?fecha=
 *
 * Diseñado para minimizar llamadas HTTP: una llamada al cargar el mes
 * y otra al seleccionar un día.
 */
@RestController
@RequestMapping("/calendario")
@RequiredArgsConstructor
public class CalendarioController {

    private final CalendarioService calendarioService;

    // ─── Nutriólogo ────────────────────────────────────────────────────────────

    /**
     * Resumen mensual para el nutriólogo autenticado.
     * Devuelve indicadores por día para poblar el grid del calendario.
     *
     * @param year  Año (ej: 2026)
     * @param month Mes 1-12 (ej: 8 para agosto)
     */
    @GetMapping("/nutriologo/mes")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<CalendarioMesNutriologoResponse>> getMesNutriologo(
            @RequestParam int year,
            @RequestParam int month) {

        CalendarioMesNutriologoResponse response = calendarioService.getMesNutriologo(year, month);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Resumen mensual del nutriólogo obtenido exitosamente"));
    }

    /**
     * Detalle del día para el nutriólogo autenticado.
     * Devuelve citas, horas libres y pacientes sin diario.
     *
     * @param fecha Fecha en formato YYYY-MM-DD (ej: 2026-08-09)
     */
    @GetMapping("/nutriologo/dia")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<CalendarioDiaNutriologoResponse>> getDiaNutriologo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        CalendarioDiaNutriologoResponse response = calendarioService.getDiaNutriologo(fecha);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Detalle del día para el nutriólogo obtenido exitosamente"));
    }

    // ─── Paciente ──────────────────────────────────────────────────────────────

    /**
     * Resumen mensual para el paciente autenticado.
     * Devuelve indicadores por día para poblar el grid del calendario.
     *
     * @param year  Año (ej: 2026)
     * @param month Mes 1-12 (ej: 8 para agosto)
     */
    @GetMapping("/paciente/mes")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<CalendarioMesPacienteResponse>> getMesPaciente(
            @RequestParam int year,
            @RequestParam int month) {

        CalendarioMesPacienteResponse response = calendarioService.getMesPaciente(year, month);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Resumen mensual del paciente obtenido exitosamente"));
    }

    /**
     * Detalle del día para el paciente autenticado.
     * Devuelve citas, comidas programadas, registros del diario y bienestar.
     *
     * @param fecha Fecha en formato YYYY-MM-DD (ej: 2026-08-09)
     */
    @GetMapping("/paciente/dia")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ApiResponse<CalendarioDiaPacienteResponse>> getDiaPaciente(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        CalendarioDiaPacienteResponse response = calendarioService.getDiaPaciente(fecha);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Detalle del día para el paciente obtenido exitosamente"));
    }
}
