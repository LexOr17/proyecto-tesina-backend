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

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<AdminDashboardStatsDTO>> obtenerStatsAdmin() {
        AdminDashboardStatsDTO stats = adminDashboardService.obtenerStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Estadísticas del administrador obtenidas exitosamente"));
    }
}
