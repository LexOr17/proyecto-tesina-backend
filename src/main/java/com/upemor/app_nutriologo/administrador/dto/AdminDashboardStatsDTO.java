package com.upemor.app_nutriologo.administrador.dto;

/**
 * DTO inmutable (record) que agrupa las estadísticas principales y métricas globales para el panel de administración.
 *
 * @param totalUsuarios número total de cuentas de usuarios registrados en el sistema.
 * @param totalNutriologos cantidad total de nutriólogos activos registrados.
 * @param totalPacientes cantidad total de pacientes activos registrados.
 * @param citasHoy número de citas médicas programadas para la fecha actual.
 * @param usuariosActivos total de usuarios con cuenta en estado activo.
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public record AdminDashboardStatsDTO(
        long totalUsuarios,
        long totalNutriologos,
        long totalPacientes,
        long citasHoy,
        long usuariosActivos
) {}
