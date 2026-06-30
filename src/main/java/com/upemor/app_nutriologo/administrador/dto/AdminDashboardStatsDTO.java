package com.upemor.app_nutriologo.administrador.dto;

public record AdminDashboardStatsDTO(
        long totalUsuarios,
        long totalNutriologos,
        long totalPacientes,
        long citasHoy,
        long usuariosActivos
) {}
