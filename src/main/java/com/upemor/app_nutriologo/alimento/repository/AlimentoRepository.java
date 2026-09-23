package com.upemor.app_nutriologo.alimento.repository;

import com.upemor.app_nutriologo.alimento.entity.Alimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad {@link Alimento}.
 * <p>
 * Proporciona métodos de consulta para la búsqueda, validación de nombres únicos,
 * filtrado por categoría y agregaciones estadísticas sobre el catálogo de alimentos.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface AlimentoRepository extends JpaRepository<Alimento, Long> {

    /**
     * Localiza un alimento a partir de su nombre exacto.
     *
     * @param nombre nombre del alimento a consultar.
     * @return un {@link Optional} con el {@link Alimento} si fue hallado, o vacío en caso contrario.
     */
    Optional<Alimento> findByNombre(String nombre);

    /**
     * Recupera la lista de alimentos que pertenecen a una categoría específica.
     *
     * @param categoria nombre del grupo o categoría nutricional.
     * @return lista de {@link Alimento} pertenecientes a la categoría.
     */
    List<Alimento> findByCategoria(String categoria);

    /**
     * Verifica la existencia de un alimento con el nombre indicado para evitar registros duplicados.
     *
     * @param nombre nombre del alimento a comprobar.
     * @return {@code true} si ya existe un alimento con ese nombre, {@code false} de lo contrario.
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Realiza una búsqueda paginada de alimentos cuyo nombre o categoría contengan la palabra clave proporcionada.
     *
     * @param keyword texto de búsqueda.
     * @param pageable configuración de paginación y ordenamiento.
     * @return página {@link Page} de {@link Alimento} coincidentes.
     */
    @Query("SELECT a FROM Alimento a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.categoria) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Alimento> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Cuenta el número total de alimentos registrados por un usuario en particular.
     *
     * @param usuarioId identificador primario del usuario creador.
     * @return cantidad de alimentos dados de alta por el usuario.
     */
    @Query("SELECT COUNT(a) FROM Alimento a WHERE a.registradoPor.id = :usuarioId")
    long countByRegistradoPorId(@Param("usuarioId") Long usuarioId);

    /**
     * Agrupa y contabiliza los alimentos dados de alta por un usuario específico según su categoría.
     *
     * @param usuarioId identificador del usuario creador.
     * @return lista de arreglos donde la primera posición es la categoría y la segunda el conteo total.
     */
    @Query("SELECT a.categoria, COUNT(a) FROM Alimento a WHERE a.registradoPor.id = :usuarioId GROUP BY a.categoria ORDER BY COUNT(a) DESC")
    List<Object[]> countAlimentosPorCategoriaByRegistradoPorId(@Param("usuarioId") Long usuarioId);

    /**
     * Agrupa y contabiliza la totalidad de alimentos en el catálogo según su categoría a nivel global.
     *
     * @return lista de arreglos donde la primera posición es la categoría y la segunda el conteo total.
     */
    @Query("SELECT a.categoria, COUNT(a) FROM Alimento a GROUP BY a.categoria ORDER BY COUNT(a) DESC")
    List<Object[]> countAlimentosPorCategoriaGlobal();
}
