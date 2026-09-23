package com.upemor.app_nutriologo.nutriologo.repository;

import com.upemor.app_nutriologo.nutriologo.dto.NutriologoSelectorDTO;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad {@link Nutriologo}.
 * <p>
 * Proporciona métodos de consulta y proyecciones optimizadas para la gestión y asignación
 * de nutriólogos en la plataforma.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public interface NutriologoRepository extends JpaRepository<Nutriologo, Long> {

    /**
     * Obtiene una lista simplificada y ordenada de todos los nutriólogos para su uso en selectores y formularios.
     *
     * @param nombre criterio de ordenamiento a aplicar a la consulta.
     * @return lista de {@link NutriologoSelectorDTO} con el identificador y nombres de los profesionales.
     */
    @Query("SELECT new com.upemor.app_nutriologo.nutriologo.dto.NutriologoSelectorDTO(n.id, n.nombre, n.apellidoPaterno) FROM Nutriologo n")
    List<NutriologoSelectorDTO> selectorNutriologo(Sort nombre);

}
