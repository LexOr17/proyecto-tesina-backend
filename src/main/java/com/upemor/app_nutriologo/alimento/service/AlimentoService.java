package com.upemor.app_nutriologo.alimento.service;

import com.upemor.app_nutriologo.alimento.dto.AlimentoRequestDTO;
import com.upemor.app_nutriologo.alimento.dto.AlimentoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Contrato de servicios para la gestión y administración del catálogo de alimentos.
 * <p>
 * Define las operaciones de negocio para dar de alta alimentos con soporte de carga de imágenes,
 * actualización de información nutricional, búsquedas paginadas multicriterio y eliminación.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface AlimentoService {

    /**
     * Registra un nuevo alimento en el catálogo, procesa su imagen adjunta y asocia la autoría al usuario emisor.
     *
     * @param requestDTO datos nutricionales y de porción del alimento.
     * @param imagen archivo de imagen opcional para ilustrar el alimento.
     * @param correoUsuario correo electrónico del usuario que realiza el registro.
     * @return {@link AlimentoResponseDTO} con los datos del alimento creado.
     * @throws IOException si se produce un error durante el almacenamiento físico del archivo de imagen.
     */
    AlimentoResponseDTO registrarAlimento(AlimentoRequestDTO requestDTO, MultipartFile imagen, String correoUsuario) throws IOException;

    /**
     * Recupera una lista paginada de todos los alimentos disponibles en el catálogo.
     *
     * @param pageable configuración de paginación y ordenamiento.
     * @return página {@link Page} de {@link AlimentoResponseDTO}.
     */
    Page<AlimentoResponseDTO> listarAlimentos(Pageable pageable);

    /**
     * Realiza una búsqueda paginada de alimentos que coincidan por nombre o categoría con la palabra clave dada.
     *
     * @param keyword texto o término de coincidencia.
     * @param pageable configuración de paginación y ordenamiento.
     * @return página {@link Page} de {@link AlimentoResponseDTO} coincidentes.
     */
    Page<AlimentoResponseDTO> buscarAlimentosPorPalabraClave(String keyword, Pageable pageable);

    /**
     * Obtiene el listado completo de alimentos clasificados bajo una categoría específica.
     *
     * @param categoria nombre del grupo o categoría nutricional.
     * @return lista de {@link AlimentoResponseDTO} correspondientes.
     */
    List<AlimentoResponseDTO> buscarPorCategoria(String categoria);

    /**
     * Localiza un alimento a partir de su identificador primario.
     *
     * @param id identificador primario del alimento.
     * @return {@link AlimentoResponseDTO} correspondiente al alimento encontrado.
     */
    AlimentoResponseDTO buscarPorId(Long id);

    /**
     * Actualiza la información nutricional de un alimento existente y opcionalmente sustituye su imagen.
     *
     * @param id identificador primario del alimento a modificar.
     * @param requestDTO datos nutricionales actualizados.
     * @param imagen nuevo archivo de imagen opcional a asociar.
     * @return {@link AlimentoResponseDTO} con los datos actualizados.
     * @throws IOException si ocurre un error al manipular el archivo físico de la nueva imagen.
     */
    AlimentoResponseDTO actualizarAlimento(Long id, AlimentoRequestDTO requestDTO, MultipartFile imagen) throws IOException;

    /**
     * Elimina un alimento del catálogo a partir de su identificador primario.
     *
     * @param id identificador primario del alimento a remover.
     */
    void eliminarAlimento(Long id);
}
