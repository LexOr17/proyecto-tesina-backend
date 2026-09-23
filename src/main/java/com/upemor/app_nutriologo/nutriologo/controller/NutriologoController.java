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

/**
 * Controlador REST para las operaciones vinculadas al rol del Nutriólogo.
 * <p>
 * Proporciona endpoints para la obtención de listas de selectores y la administración
 * de la cartera de pacientes asignados (registro, consulta paginada, detalle, actualización y eliminación).
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@RestController
@RequestMapping("/nutriologos")
@RequiredArgsConstructor
public class NutriologoController {

    private final ServiceNutriologo serviceNutriologo;

    /**
     * Obtiene la lista simplificada de nutriólogos activos para menús de selección.
     *
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo la lista de {@link NutriologoSelectorDTO}.
     */
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

    /**
     * Da de alta a un nuevo paciente y lo vincula automáticamente al nutriólogo autenticado.
     *
     * @param pacienteDTO cuerpo de la solicitud con los datos del paciente.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo el paciente registrado y HTTP 201.
     */
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

    /**
     * Obtiene el listado paginado de los pacientes asignados al nutriólogo en sesión.
     *
     * @param pageable opciones de paginación y ordenamiento.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo la página de pacientes.
     */
    @GetMapping("/pacientes")
    @PreAuthorize("hasAnyRole('NUTRIOLOGO', 'ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Page<UsuarioResponseDTO>>> listarPacientesAsignados(Pageable pageable) {
        Page<UsuarioResponseDTO> pacientes = serviceNutriologo.listarPacientesAsignados(pageable);
        ApiResponse<Page<UsuarioResponseDTO>> response = ApiResponse.<Page<UsuarioResponseDTO>>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Pacientes listados exitosamente")
                .datos(pacientes)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Consulta los detalles de un paciente específico de la cartera del nutriólogo.
     *
     * @param idPaciente identificador primario del paciente.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo los datos del paciente.
     */
    @GetMapping("/pacientes/{idPaciente}")
//    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> buscarPacienteAsignado(@PathVariable Long idPaciente) {
        UsuarioResponseDTO paciente = serviceNutriologo.buscarPacienteAsignado(idPaciente);
        ApiResponse<UsuarioResponseDTO> response = ApiResponse.<UsuarioResponseDTO>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje("Paciente encontrado")
                .datos(paciente)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza la información personal de un paciente asignado.
     *
     * @param idPaciente identificador primario del paciente a modificar.
     * @param pacienteDTO cuerpo de la solicitud con los nuevos datos.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo el paciente actualizado.
     */
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

    /**
     * Elimina el registro de un paciente asignado al nutriólogo.
     *
     * @param idPaciente identificador primario del paciente a eliminar.
     * @return {@link ResponseEntity} con código HTTP 204 No Content.
     */
    @DeleteMapping("/pacientes/{idPaciente}")
    @PreAuthorize("hasRole('NUTRIOLOGO')")
    public ResponseEntity<Void> eliminarPacienteAsignado(@PathVariable Long idPaciente) {
        serviceNutriologo.eliminarPacienteAsignado(idPaciente);
        return ResponseEntity.noContent().build();
    }
}
