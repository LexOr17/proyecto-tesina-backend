package com.upemor.app_nutriologo.plantillas.service;

import com.upemor.app_nutriologo.plantillas.dto.PlantillaRequestDTO;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfaz de servicio para la gestión de plantillas de comidas nutricionales.
 * <p>
 * Define las operaciones disponibles para crear, buscar con paginación,
 * consultar, actualizar y eliminar plantillas de comidas predefinidas.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface PlantillaService {

    /**
     * Crea y almacena una nueva plantilla de comida vinculando los alimentos especificados.
     *
     * @param requestDTO Datos de la plantilla y lista de alimentos con sus cantidades.
     * @return DTO con la información de la plantilla creada y sus cálculos nutricionales.
     */
    PlantillaResponseDTO crearPlantilla(PlantillaRequestDTO requestDTO);

    /**
     * Obtiene el catálogo paginado de todas las plantillas disponibles en el sistema.
     *
     * @param pageable Configuración de paginación y ordenamiento.
     * @return Página de DTOs con las plantillas registradas.
     */
    Page<PlantillaResponseDTO> obtenerPlantillas(Pageable pageable);

    /**
     * Busca plantillas de comida aplicando filtros por coincidencia de texto y categoría/tipo de comida.
     *
     * @param keyword   Palabra clave a buscar en nombre, tipo o ingredientes.
     * @param categoria Nombre o representación textual del tipo de comida (opcional).
     * @param pageable  Configuración de paginación y ordenamiento.
     * @return Página de DTOs con las plantillas encontradas.
     */
    Page<PlantillaResponseDTO> buscarPlantillas(String keyword, String categoria, Pageable pageable);

    /**
     * Busca y obtiene una plantilla por su identificador único.
     *
     * @param id Identificador único de la plantilla.
     * @return DTO con la información detallada de la plantilla encontrada.
     */
    PlantillaResponseDTO buscarPlantillaPorId(Long id);

    /**
     * Actualiza la información, tipo de comida y alimentos de una plantilla existente.
     *
     * @param id         Identificador único de la plantilla a modificar.
     * @param requestDTO Nuevos datos y alimentos para la plantilla.
     * @return DTO con la plantilla actualizada y nuevos cálculos nutricionales.
     */
    PlantillaResponseDTO actualizarPlantilla(Long id, PlantillaRequestDTO requestDTO);

    /**
     * Elimina una plantilla del sistema por su identificador único.
     *
     * @param id Identificador único de la plantilla a eliminar.
     */
    void eliminarPlantilla(Long id);
}
