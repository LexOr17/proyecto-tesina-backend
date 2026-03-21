package com.upemor.app_nutriologo.plantillas.mapper;

import com.upemor.app_nutriologo.plantillas.dto.*;
import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import com.upemor.app_nutriologo.plantillas.entity.PlantillaAlimento;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PlantillaMapper {

    @Mapping(target = "totalCalorias", source = "detalles", qualifiedByName = "calcularCalorias")
    @Mapping(target = "totalProteinas", source = "detalles", qualifiedByName = "calcularProteinas")
    @Mapping(target = "totalCarbohidratos", source = "detalles", qualifiedByName = "calcularCarbos")
    @Mapping(target = "totalGrasas", source = "detalles", qualifiedByName = "calcularGrasas")
    PlantillaResponseDTO toDto(Plantilla plantilla);

    @Mapping(source = "alimento.id", target = "alimentoId")
    @Mapping(source = "alimento.nombre", target = "nombreAlimento")
    @Mapping(source = "alimento.unidadPorcion", target = "unidadPorcion")
    PlantillaAlimentoResponseDTO toAlimentoDto(PlantillaAlimento detalle);

    // --- LÓGICA DE CÁLCULO REUTILIZABLE ---

    @Named("calcularCalorias")
    default BigDecimal calcularCalorias(List<PlantillaAlimento> detalles) {
        return sumarNutriente(detalles, "calorias");
    }

    @Named("calcularProteinas")
    default BigDecimal calcularProteinas(List<PlantillaAlimento> detalles) {
        return sumarNutriente(detalles, "proteinas");
    }

    @Named("calcularCarbos")
    default BigDecimal calcularCarbos(List<PlantillaAlimento> detalles) {
        return sumarNutriente(detalles, "carbos");
    }

    @Named("calcularGrasas")
    default BigDecimal calcularGrasas(List<PlantillaAlimento> detalles) {
        return sumarNutriente(detalles, "grasas");
    }

    /**
     * Método privado de apoyo para no repetir código matemático
     */
    default BigDecimal sumarNutriente(List<PlantillaAlimento> detalles, String tipo) {
        if (detalles == null) return BigDecimal.ZERO;

        BigDecimal total = BigDecimal.ZERO;
        for (PlantillaAlimento d : detalles) {
            // Factor = Cantidad elegida / Porción base del alimento
            BigDecimal factor = d.getCantidad().divide(d.getAlimento().getPorcionBase(), 4, RoundingMode.HALF_UP);

            BigDecimal valorBase = switch (tipo) {
                case "calorias" -> d.getAlimento().getCalorias();
                case "proteinas" -> d.getAlimento().getProteina();
                case "carbos" -> d.getAlimento().getCarbohidratos();
                case "grasas" -> d.getAlimento().getGrasas();
                default -> BigDecimal.ZERO;
            };

            total = total.add(valorBase.multiply(factor));
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    Plantilla toEntity(PlantillaRequestDTO dto);
}