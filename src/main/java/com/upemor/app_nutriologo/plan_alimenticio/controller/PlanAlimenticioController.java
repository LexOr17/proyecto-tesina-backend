package com.upemor.app_nutriologo.plan_alimenticio.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.plan_alimenticio.dtos.PlanAlimenticioRequestDTO;
import com.upemor.app_nutriologo.plan_alimenticio.dtos.PlanAlimenticioResponseDTO;
import com.upemor.app_nutriologo.plan_alimenticio.service.PlanAlimenticioServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planes-alimenticios")
@RequiredArgsConstructor
public class PlanAlimenticioController {

    private final PlanAlimenticioServiceImpl planAlimenticioService;

    @PostMapping
    public ResponseEntity<ApiResponse<PlanAlimenticioResponseDTO>> crearPlan(@Valid @RequestBody PlanAlimenticioRequestDTO dto) {
        PlanAlimenticioResponseDTO nuevoPlan = planAlimenticioService.crearPlan(dto);
        return new ResponseEntity<>(
                ApiResponse.created(nuevoPlan, "Plan alimenticio creado exitosamente"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlanAlimenticioResponseDTO>> obtenerPlanPorId(@PathVariable Long id) {
        PlanAlimenticioResponseDTO plan = planAlimenticioService.obtenerPlanPorId(id);
        return ResponseEntity.ok(ApiResponse.success(plan, "Plan alimenticio obtenido correctamente"));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<ApiResponse<List<PlanAlimenticioResponseDTO>>> obtenerPlanesDePaciente(@PathVariable Long pacienteId) {
        List<PlanAlimenticioResponseDTO> planes = planAlimenticioService.obtenerPlanesDePaciente(pacienteId);
        return ResponseEntity.ok(ApiResponse.success(planes, "Planes alimenticios obtenidos correctamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlanAlimenticioResponseDTO>> actualizarPlan(
            @PathVariable Long id,
            @Valid @RequestBody PlanAlimenticioRequestDTO dto) {
        PlanAlimenticioResponseDTO planActualizado = planAlimenticioService.actualizarPlan(id, dto);
        return ResponseEntity.ok(ApiResponse.success(planActualizado, "Plan alimenticio actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarPlan(@PathVariable Long id) {
        planAlimenticioService.eliminarPlan(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Plan alimenticio eliminado exitosamente"));
    }
}
