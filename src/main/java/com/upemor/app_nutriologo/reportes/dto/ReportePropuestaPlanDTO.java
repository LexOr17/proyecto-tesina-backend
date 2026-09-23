package com.upemor.app_nutriologo.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportePropuestaPlanDTO {

    // Metadatos
    private Long nutriologoId;
    private String nombreNutriologo;
    private String correoNutriologo;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;

    // 1. Tasa de Solicitud de Cambio y Métricas Generales
    private long totalPacientes;
    private long pacientesConSolicitud;
    private double tasaSolicitudCambio;
    private long totalSolicitudes;
    private double promedioSolicitudesPorPaciente;
    private long solicitudesAprobadas;
    private long solicitudesRechazadas;
    private long solicitudesPendientes;
    private double tasaAprobacion;

    // 2. Gráfica y Desglose de Razones Principales ("Por Qué")
    private Map<String, Long> razonesPrincipales;
    private String razonMasFrecuente;

    // 3. Alimentos Problemáticos y Plantillas Sustituidas
    private List<AlimentoSustituidoDTO> alimentosProblematicos;
    private Map<String, Long> plantillasMasModificadas;

    // 4. Perfil de Pacientes (Inducción vs Seguimiento)
    private long solicitudesPacientesNuevos;       // <= 28 días desde fechaRegistro
    private long solicitudesPacientesSeguimiento;  // > 28 días desde fechaRegistro
    private double porcentajePacientesNuevos;
    private double porcentajePacientesSeguimiento;
    private long totalPacientesNuevos;
    private long totalPacientesSeguimiento;
    private String diagnosticoPerfil;
    private String diagnosticoReceptividad;
}
