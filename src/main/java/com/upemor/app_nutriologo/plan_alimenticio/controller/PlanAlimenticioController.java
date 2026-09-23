package com.upemor.app_nutriologo.plan_alimenticio.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.plan_alimenticio.dto.PlanAlimenticioRequestDTO;
import com.upemor.app_nutriologo.plan_alimenticio.dto.PlanAlimenticioResponseDTO;
import com.upemor.app_nutriologo.plan_alimenticio.service.PlanAlimenticioService; // Importamos la Interfaz
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de planes alimenticios.
 * <p>
 * Proporciona endpoints para crear, consultar, actualizar y eliminar planes
 * alimenticios asignados a pacientes por sus respectivos nutriólogos.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@RestController
@RequestMapping("/planes-alimenticios")
@RequiredArgsConstructor
public class PlanAlimenticioController {

    private final PlanAlimenticioService planAlimenticioService;

    /**
     * Registra un nuevo plan alimenticio en el sistema con sus comidas programadas.
     *
     * @param dto DTO con los datos del plan alimenticio y la lista de comidas a programar.
     * @return {@link ResponseEntity} con la respuesta estandarizada que contiene el plan creado y estado 201 (CREATED).
     */
    @PostMapping
    @PreAuthorize("hasRole('NUTRIOLOGO')") // Solo el nutriólogo crea planes
    public ResponseEntity<ApiResponse<PlanAlimenticioResponseDTO>> crearPlan(
            @Valid @RequestBody PlanAlimenticioRequestDTO dto) {

        PlanAlimenticioResponseDTO nuevoPlan = planAlimenticioService.crearPlan(dto);
        return new ResponseEntity<>(
                ApiResponse.created(nuevoPlan, "Plan alimenticio creado exitosamente"),
                HttpStatus.CREATED
        );
    }

    /**
     * Obtiene la información detallada de un plan alimenticio a partir de su identificador.
     *
     * @param id Identificador único del plan alimenticio.
     * @return {@link ResponseEntity} con la respuesta estandarizada que contiene el plan encontrado y estado 200 (OK).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<PlanAlimenticioResponseDTO>> obtenerPlanPorId(@PathVariable Long id) {
        PlanAlimenticioResponseDTO plan = planAlimenticioService.obtenerPlanPorId(id);
        return ResponseEntity.ok(ApiResponse.success(plan, "Plan alimenticio obtenido correctamente"));
    }

    /**
     * Obtiene la lista de planes alimenticios registrados para un paciente determinado.
     *
     * @param pacienteId Identificador único del paciente.
     * @return {@link ResponseEntity} con la lista de planes asociados al paciente y estado 200 (OK).
     */
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<List<PlanAlimenticioResponseDTO>>> obtenerPlanesDePaciente(@PathVariable Long pacienteId) {
        List<PlanAlimenticioResponseDTO> planes = planAlimenticioService.obtenerPlanesDePaciente(pacienteId);
        return ResponseEntity.ok(ApiResponse.success(planes, "Planes alimenticios obtenidos correctamente"));
    }

    /**
     * Actualiza la información y comidas programadas de un plan alimenticio existente.
     *
     * @param id Identificador único del plan alimenticio a modificar.
     * @param dto DTO con los datos actualizados del plan.
     * @return {@link ResponseEntity} con la información del plan alimenticio actualizado y estado 200 (OK).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<PlanAlimenticioResponseDTO>> actualizarPlan(
            @PathVariable Long id,
            @Valid @RequestBody PlanAlimenticioRequestDTO dto) {

        PlanAlimenticioResponseDTO planActualizado = planAlimenticioService.actualizarPlan(id, dto);
        return ResponseEntity.ok(ApiResponse.success(planActualizado, "Plan alimenticio actualizado exitosamente"));
    }

    /**
     * Elimina un plan alimenticio existente del sistema.
     *
     * @param id Identificador único del plan alimenticio a eliminar.
     * @return {@link ResponseEntity} con respuesta sin contenido y estado 200 (OK).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<Void>> eliminarPlan(@PathVariable Long id) {
        planAlimenticioService.eliminarPlan(id);
        // Usamos el método noContent que ya tienes en tu ApiResponse
        return ResponseEntity.ok(ApiResponse.noContent("Plan alimenticio eliminado exitosamente"));
    }
}