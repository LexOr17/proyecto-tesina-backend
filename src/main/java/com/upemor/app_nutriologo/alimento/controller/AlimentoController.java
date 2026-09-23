package com.upemor.app_nutriologo.alimento.controller;

import com.upemor.app_nutriologo.alimento.dto.AlimentoRequestDTO;
import com.upemor.app_nutriologo.alimento.dto.AlimentoResponseDTO;
import com.upemor.app_nutriologo.alimento.service.AlimentoService;
import com.upemor.app_nutriologo.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controlador REST para la gestión del catálogo de alimentos.
 * <p>
 * Proporciona endpoints para el registro con soporte multipart (datos e imagen),
 * consulta paginada, búsqueda por palabra clave, filtrado por categoría, detalle por ID,
 * actualización y eliminación de alimentos.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/alimentos")
public class AlimentoController {

    private final AlimentoService alimentoService;

    /**
     * Registra un nuevo alimento en el catálogo recibiendo datos y archivo de imagen opcional.
     *
     * @param requestDTO datos nutricionales y de porción del alimento.
     * @param imagen archivo de imagen opcional para ilustrar el alimento.
     * @param authentication contexto de autenticación para obtener el correo del usuario emisor.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo el alimento creado y estado HTTP 201.
     * @throws IOException si ocurre un error al procesar el archivo de imagen.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<AlimentoResponseDTO>> registrarAlimento(
            @Valid @RequestPart("datos") AlimentoRequestDTO requestDTO,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen,
            Authentication authentication) throws IOException {

        String correoUsuario = authentication.getName();
        AlimentoResponseDTO nuevoAlimento = alimentoService.registrarAlimento(requestDTO, imagen, correoUsuario);

        ApiResponse<AlimentoResponseDTO> response = ApiResponse.created(nuevoAlimento,
                "Alimento registrado exitosamente");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtiene el catálogo completo de alimentos de forma paginada.
     *
     * @param pageable configuración de paginación y ordenamiento.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo la página de alimentos.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<Page<AlimentoResponseDTO>>> listarAlimentos(Pageable pageable) {
        Page<AlimentoResponseDTO> alimentos = alimentoService.listarAlimentos(pageable);

        ApiResponse<Page<AlimentoResponseDTO>> response = ApiResponse.success(alimentos, "Listado de alimentos");
        return ResponseEntity.ok(response);
    }

    /**
     * Realiza una búsqueda paginada de alimentos mediante una palabra clave que coincide con su nombre o categoría.
     *
     * @param keyword texto de búsqueda.
     * @param pageable configuración de paginación y ordenamiento.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo los alimentos coincidentes.
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<Page<AlimentoResponseDTO>>> buscarAlimentos(
            @RequestParam("keyword") String keyword, Pageable pageable) {
        Page<AlimentoResponseDTO> alimentos = alimentoService.buscarAlimentosPorPalabraClave(keyword, pageable);

        ApiResponse<Page<AlimentoResponseDTO>> response = ApiResponse.success(alimentos, "Alimentos encontrados");
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todos los alimentos clasificados bajo una categoría específica.
     *
     * @param categoria nombre del grupo o categoría nutricional.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo la lista de alimentos.
     */
    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<List<AlimentoResponseDTO>>> buscarPorCategoria(@PathVariable String categoria) {
        List<AlimentoResponseDTO> alimentos = alimentoService.buscarPorCategoria(categoria);

        ApiResponse<List<AlimentoResponseDTO>> response = ApiResponse.success(alimentos,
                "Alimentos encontrados en la categoría: " + categoria);
        return ResponseEntity.ok(response);
    }

    /**
     * Consulta el detalle de un alimento por su identificador primario.
     *
     * @param id identificador primario del alimento.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo el alimento hallado.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO', 'PACIENTE')")
    public ResponseEntity<ApiResponse<AlimentoResponseDTO>> buscarPorId(@PathVariable Long id) {
        AlimentoResponseDTO alimento = alimentoService.buscarPorId(id);

        ApiResponse<AlimentoResponseDTO> response = ApiResponse.success(alimento, "Alimento encontrado");
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza la información nutricional de un alimento existente y opcionalmente su fotografía.
     *
     * @param id identificador primario del alimento a actualizar.
     * @param requestDTO nuevos datos nutricionales del alimento.
     * @param imagen nuevo archivo de imagen opcional.
     * @return {@link ResponseEntity} con {@link ApiResponse} conteniendo el alimento actualizado.
     * @throws IOException si ocurre un fallo al manipular la imagen.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<AlimentoResponseDTO>> actualizarAlimento(
            @PathVariable Long id,
            @Valid @RequestPart("datos") AlimentoRequestDTO requestDTO,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) throws IOException {

        AlimentoResponseDTO alimentoActualizado = alimentoService.actualizarAlimento(id, requestDTO, imagen);

        ApiResponse<AlimentoResponseDTO> response = ApiResponse.success(alimentoActualizado,
                "Alimento actualizado exitosamente");
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina un alimento del catálogo a partir de su identificador primario.
     *
     * @param id identificador primario del alimento a remover.
     * @return {@link ResponseEntity} con {@link ApiResponse} y código HTTP 204 No Content.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'NUTRIOLOGO')")
    public ResponseEntity<ApiResponse<Void>> eliminarAlimento(@PathVariable Long id) {
        alimentoService.eliminarAlimento(id);

        ApiResponse<Void> response = ApiResponse.noContent("Alimento eliminado exitosamente");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }
}
