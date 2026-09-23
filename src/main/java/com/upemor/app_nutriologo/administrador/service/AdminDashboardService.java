package com.upemor.app_nutriologo.administrador.service;

import com.upemor.app_nutriologo.administrador.dto.AdminDashboardStatsDTO;
import com.upemor.app_nutriologo.citas.repository.CitaRepository;
import com.upemor.app_nutriologo.nutriologo.repository.NutriologoRepository;
import com.upemor.app_nutriologo.paciente.repository.PacienteRepository;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Servicio encargado de calcular y recopilar estadísticas e indicadores globales para el panel de administración.
 * <p>
 * Agrega información desde múltiples repositorios ({@link UsuarioRepository}, {@link NutriologoRepository},
 * {@link PacienteRepository}, {@link CitaRepository}) para suministrar un resumen cuantitativo en tiempo real.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UsuarioRepository usuarioRepository;
    private final NutriologoRepository nutriologoRepository;
    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;

    /**
     * Recopila y consolida las métricas globales del sistema para el panel administrativo.
     *
     * @return {@link AdminDashboardStatsDTO} con las métricas consolidadas (totales de usuarios, citas del día y activos).
     */
    @Transactional(readOnly = true)
    public AdminDashboardStatsDTO obtenerStats() {
        long totalUsuarios = usuarioRepository.count();
        long totalNutriologos = nutriologoRepository.count();
        long totalPacientes = pacienteRepository.count();

        // Citas de hoy: inicio del día hasta fin del día
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1).minusNanos(1);
        long citasHoy = citaRepository.countCitasHoy(inicioDia, finDia);

        // Usuarios activos (cuenta los que tienen activo = true)
        long usuariosActivos = usuarioRepository.countByActivoTrue();

        return new AdminDashboardStatsDTO(
                totalUsuarios,
                totalNutriologos,
                totalPacientes,
                citasHoy,
                usuariosActivos);
    }
}
