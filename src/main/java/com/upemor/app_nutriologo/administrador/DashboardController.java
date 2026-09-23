package com.upemor.app_nutriologo.administrador;

import com.upemor.app_nutriologo.administrador.dto.AdminDashboardStatsDTO;
import com.upemor.app_nutriologo.administrador.service.AdminDashboardService;
import com.upemor.app_nutriologo.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la consulta de estadísticas y métricas del panel administrativo.
 * <p>
 * Proporciona endpoints protegidos exclusivos para el rol de administrador con datos cuantitativos en tiempo real.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AdminDashboardService adminDashboardService;

    /**
     * Endpoint para obtener el consolidado estadístico del panel de control del administrador.
     *
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo {@link AdminDashboardStatsDTO}.
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<AdminDashboardStatsDTO>> obtenerStatsAdmin() {
        AdminDashboardStatsDTO stats = adminDashboardService.obtenerStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Estadísticas del administrador obtenidas exitosamente"));
    }
}
