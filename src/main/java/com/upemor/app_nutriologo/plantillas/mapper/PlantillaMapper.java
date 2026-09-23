package com.upemor.app_nutriologo.plantillas.mapper;

import com.upemor.app_nutriologo.plantillas.dto.*;
import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import com.upemor.app_nutriologo.plantillas.entity.PlantillaAlimento;
import com.upemor.app_nutriologo.utils.NutrienteCalculator;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapper MapStruct para la conversión entre entidades de plantillas y sus respectivos DTOs.
 * <p>
 * Realiza el cálculo automático de macronutrientes totales (calorías, proteínas,
 * carbohidratos y grasas) acumulando los aportes nutricionales de cada alimento
 * detallado mediante {@link NutrienteCalculator}.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface PlantillaMapper {

    /**
     * Mapea una entidad {@link Plantilla} a su representación {@link PlantillaResponseDTO},
     * calculando los totales acumulados de macronutrientes.
     *
     * @param plantilla Entidad de la plantilla a mapear.
     * @return DTO con la información detallada y totales nutricionales de la plantilla.
     */
    @Mapping(target = "totalCalorias", source = "detalles", qualifiedByName = "calcularCalorias")
    @Mapping(target = "totalProteinas", source = "detalles", qualifiedByName = "calcularProteinas")
    @Mapping(target = "totalCarbohidratos", source = "detalles", qualifiedByName = "calcularCarbos")
    @Mapping(target = "totalGrasas", source = "detalles", qualifiedByName = "calcularGrasas")
    PlantillaResponseDTO toDto(Plantilla plantilla);

    /**
     * Mapea un detalle de alimento {@link PlantillaAlimento} a {@link PlantillaAlimentoResponseDTO}.
     *
     * @param detalle Registro de detalle que asocia el alimento a la plantilla.
     * @return DTO con los datos del alimento y la cantidad asignada.
     */
    @Mapping(source = "alimento.id", target = "alimentoId")
    @Mapping(source = "alimento.nombre", target = "nombreAlimento")
    @Mapping(source = "alimento.porcionBase", target = "porcionBase")
    @Mapping(source = "alimento.unidadPorcion", target = "unidadPorcion")
    PlantillaAlimentoResponseDTO toAlimentoDto(PlantillaAlimento detalle);

    /**
     * Convierte un DTO {@link PlantillaRequestDTO} a la entidad {@link Plantilla}.
     *
     * @param dto DTO con los datos de creación de la plantilla.
     * @return Entidad {@link Plantilla} inicializada.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    Plantilla toEntity(PlantillaRequestDTO dto);

    // --- Cálculos nutricionales delegados a NutrienteCalculator ---

    /**
     * Calcula la suma total de calorías aportadas por los alimentos de la plantilla.
     *
     * @param detalles Lista de detalles de alimentos de la plantilla.
     * @return Total acumulado de calorías (kcal).
     */
    @Named("calcularCalorias")
    default BigDecimal calcularCalorias(List<PlantillaAlimento> detalles) {
        return NutrienteCalculator.sumar(detalles,
                a -> a.getCantidad(),
                d -> d.getAlimento().getPorcionBase(),
                d -> d.getAlimento().getCalorias());
    }

    /**
     * Calcula la suma total de proteínas aportadas por los alimentos de la plantilla.
     *
     * @param detalles Lista de detalles de alimentos de la plantilla.
     * @return Total acumulado de proteínas (g).
     */
    @Named("calcularProteinas")
    default BigDecimal calcularProteinas(List<PlantillaAlimento> detalles) {
        return NutrienteCalculator.sumar(detalles,
                a -> a.getCantidad(),
                d -> d.getAlimento().getPorcionBase(),
                d -> d.getAlimento().getProteina());
    }

    /**
     * Calcula la suma total de carbohidratos aportados por los alimentos de la plantilla.
     *
     * @param detalles Lista de detalles de alimentos de la plantilla.
     * @return Total acumulado de carbohidratos (g).
     */
    @Named("calcularCarbos")
    default BigDecimal calcularCarbos(List<PlantillaAlimento> detalles) {
        return NutrienteCalculator.sumar(detalles,
                a -> a.getCantidad(),
                d -> d.getAlimento().getPorcionBase(),
                d -> d.getAlimento().getCarbohidratos());
    }

    /**
     * Calcula la suma total de grasas aportadas por los alimentos de la plantilla.
     *
     * @param detalles Lista de detalles de alimentos de la plantilla.
     * @return Total acumulado de grasas (g).
     */
    @Named("calcularGrasas")
    default BigDecimal calcularGrasas(List<PlantillaAlimento> detalles) {
        return NutrienteCalculator.sumar(detalles,
                a -> a.getCantidad(),
                d -> d.getAlimento().getPorcionBase(),
                d -> d.getAlimento().getGrasas());
    }
}