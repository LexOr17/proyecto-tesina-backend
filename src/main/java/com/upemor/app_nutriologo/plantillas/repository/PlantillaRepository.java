package com.upemor.app_nutriologo.plantillas.repository;

import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link Plantilla}.
 * <p>
 * Provee métodos para la consulta, paginación, búsqueda por coincidencia textual
 * y análisis estadístico de plantillas y alimentos asignados en el sistema.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface PlantillaRepository extends JpaRepository<Plantilla, Long> {

       /**
        * Recupera todas las plantillas creadas por un usuario específico (nutriólogo).
        *
        * @param usuarioId Identificador único del usuario creador.
        * @return Lista de plantillas pertenecientes al usuario.
        */
       List<Plantilla> findByUsuarioId(Long usuarioId);

       /**
        * Recupera de forma paginada las plantillas creadas por un usuario específico.
        *
        * @param usuarioId Identificador único del usuario creador.
        * @param pageable  Configuración de paginación y ordenamiento.
        * @return Página de plantillas pertenecientes al usuario.
        */
       Page<Plantilla> findByUsuarioId(Long usuarioId, Pageable pageable);

       /**
        * Realiza una búsqueda paginada de plantillas filtrando por palabra clave (en nombre de plantilla,
        * tipo de comida o nombre de alimento asociado) y opcionalmente por tipo de comida exacto.
        *
        * @param keyword    Palabra clave o término de búsqueda.
        * @param tipoComida Tipo de comida específico por el cual filtrar (opcional).
        * @param pageable   Configuración de paginación y ordenamiento.
        * @return Página con las plantillas que coinciden con los criterios de búsqueda.
        */
       @Query("SELECT DISTINCT p FROM Plantilla p " +
              "LEFT JOIN p.detalles d " +
              "LEFT JOIN d.alimento a " +
              "WHERE (:keyword IS NULL OR :keyword = '' OR " +
              "      LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
              "      LOWER(CAST(p.tipoComida AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
              "      LOWER(a.nombre) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
              "AND (:tipoComida IS NULL OR p.tipoComida = :tipoComida)")
       Page<Plantilla> buscarPorNombreYTipoComida(
                     @Param("keyword") String keyword,
                     @Param("tipoComida") com.upemor.app_nutriologo.plantillas.enums.TipoComida tipoComida,
                     Pageable pageable);

       /**
        * Cuenta el número total de plantillas creadas por un usuario específico.
        *
        * @param usuarioId Identificador único del usuario.
        * @return Cantidad de plantillas del usuario.
        */
       @Query("SELECT COUNT(p) FROM Plantilla p WHERE p.usuario.id = :usuarioId")
       long countByUsuarioId(@Param("usuarioId") Long usuarioId);

       /**
        * Agrupa y cuenta las plantillas de un usuario específico según su tipo de comida.
        *
        * @param usuarioId Identificador único del usuario.
        * @return Lista de arreglos de objetos conteniendo el tipo de comida y la cantidad correspondiente.
        */
       @Query("SELECT p.tipoComida, COUNT(p) FROM Plantilla p WHERE p.usuario.id = :usuarioId GROUP BY p.tipoComida")
       List<Object[]> countPlantillasPorTipoComidaByUsuarioId(@Param("usuarioId") Long usuarioId);

       /**
        * Agrupa y cuenta todas las plantillas registradas en el sistema según su tipo de comida a nivel global.
        *
        * @return Lista de arreglos de objetos conteniendo el tipo de comida y la cantidad global.
        */
       @Query("SELECT p.tipoComida, COUNT(p) FROM Plantilla p GROUP BY p.tipoComida")
       List<Object[]> countPlantillasPorTipoComidaGlobal();

       /**
        * Obtiene los alimentos más utilizados o asignados en plantillas por un nutriólogo específico,
        * ordenados de forma descendente por frecuencia de asignación.
        *
        * @param usuarioId Identificador único del nutriólogo.
        * @return Lista de arreglos de objetos con [id, nombre, categoria, conteo] del alimento.
        */
       @Query("SELECT pa.alimento.id, pa.alimento.nombre, pa.alimento.categoria, COUNT(pa) " +
              "FROM PlantillaAlimento pa " +
              "WHERE pa.plantilla.usuario.id = :usuarioId " +
              "GROUP BY pa.alimento.id, pa.alimento.nombre, pa.alimento.categoria " +
              "ORDER BY COUNT(pa) DESC")
       List<Object[]> findTopAlimentosMasAsignadosPorNutriologo(@Param("usuarioId") Long usuarioId);

       /**
        * Obtiene los alimentos más asignados en plantillas a nivel global por todos los nutriólogos,
        * ordenados de forma descendente por frecuencia de asignación.
        *
        * @return Lista de arreglos de objetos con [id, nombre, categoria, conteo] del alimento.
        */
       @Query("SELECT pa.alimento.id, pa.alimento.nombre, pa.alimento.categoria, COUNT(pa) " +
              "FROM PlantillaAlimento pa " +
              "GROUP BY pa.alimento.id, pa.alimento.nombre, pa.alimento.categoria " +
              "ORDER BY COUNT(pa) DESC")
       List<Object[]> findTopAlimentosMasAsignadosGlobal();
}
