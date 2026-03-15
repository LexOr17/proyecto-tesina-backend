package com.upemor.app_nutriologo.nutriologo.controller;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import com.upemor.app_nutriologo.nutriologo.dto.NutriologoSelectorDTO;
import com.upemor.app_nutriologo.nutriologo.service.ServiceNutriologo;
import com.upemor.app_nutriologo.paciente.dto.PacienteRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nutriologos")
@RequiredArgsConstructor
public class NutriologoController {

    private final ServiceNutriologo serviceNutriologo;

    @GetMapping("/selector")
    public ResponseEntity<ApiResponse<List<NutriologoSelectorDTO>>> obtenerSelectorNutriologos() {
        List<NutriologoSelectorDTO> listaNutriologos = serviceNutriologo.selectorDeNutriologos();
        ApiResponse<List<NutriologoSelectorDTO>> response = ApiResponse.<List<NutriologoSelectorDTO>>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Success")
                .datos(listaNutriologos)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pacientes")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> registrarPaciente(
            @Valid @RequestBody PacienteRequestDTO pacienteDTO) {
        UsuarioResponseDTO nuevoPaciente = serviceNutriologo.registrarPaciente(pacienteDTO);
        ApiResponse<UsuarioResponseDTO> response = ApiResponse.<UsuarioResponseDTO>builder()
                .estatus(HttpStatus.CREATED.value())
                .mensaje("Paciente registrado exitosamente")
                .datos(nuevoPaciente)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/pacientes")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<Page<UsuarioResponseDTO>>> listarPacientesAsignados(Pageable pageable) {
        Page<UsuarioResponseDTO> pacientes = serviceNutriologo.listarPacientesAsignados(pageable);
        ApiResponse<Page<UsuarioResponseDTO>> response = ApiResponse.<Page<UsuarioResponseDTO>>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Pacientes listados exitosamente")
                .datos(pacientes)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pacientes/{idPaciente}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> buscarPacienteAsignado(@PathVariable Long idPaciente) {
        UsuarioResponseDTO paciente = serviceNutriologo.buscarPacienteAsignado(idPaciente);
        ApiResponse<UsuarioResponseDTO> response = ApiResponse.<UsuarioResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Paciente encontrado")
                .datos(paciente)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/pacientes/{idPaciente}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> actualizarPacienteAsignado(
            @PathVariable Long idPaciente, @Valid @RequestBody PacienteRequestDTO pacienteDTO) {
        UsuarioResponseDTO pacienteActualizado = serviceNutriologo.actualizarPacienteAsignado(idPaciente, pacienteDTO);
        ApiResponse<UsuarioResponseDTO> response = ApiResponse.<UsuarioResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Paciente actualizado exitosamente")
                .datos(pacienteActualizado)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/pacientes/{idPaciente}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<Void> eliminarPacienteAsignado(@PathVariable Long idPaciente) {
        serviceNutriologo.eliminarPacienteAsignado(idPaciente);
        return ResponseEntity.noContent().build();
    }
}
