package com.upemor.app_nutriologo.sugerencia_plantilla.mapper;

import com.upemor.app_nutriologo.plantillas.dto.PlantillaAlimentoResponseDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaPlantillaResponseDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.entity.SugerenciaPlantilla;
import com.upemor.app_nutriologo.sugerencia_plantilla.entity.SugerenciaPlantillaDetalle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SugerenciaPlantillaMapper {

    @Mapping(source = "paciente.id",    target = "pacienteId")
    @Mapping(source = "nutriologo.id",  target = "nutriologoId")
    @Mapping(source = "detalles",       target = "totalCalorias",      qualifiedByName = "calcularCalorias")
    @Mapping(source = "detalles",       target = "totalProteinas",     qualifiedByName = "calcularProteinas")
    @Mapping(source = "detalles",       target = "totalCarbohidratos", qualifiedByName = "calcularCarbos")
    @Mapping(source = "detalles",       target = "totalGrasas",        qualifiedByName = "calcularGrasas")
    SugerenciaPlantillaResponseDTO toDto(SugerenciaPlantilla sugerencia);

    @Mapping(source = "alimento.id",           target = "alimentoId")
    @Mapping(source = "alimento.nombre",       target = "nombreAlimento")
    @Mapping(source = "alimento.unidadPorcion",target = "unidadPorcion")
    PlantillaAlimentoResponseDTO toDetalleDto(SugerenciaPlantillaDetalle detalle);

    // --- Cálculos nutricionales ---

    @Named("calcularCalorias")
    default BigDecimal calcularCalorias(List<SugerenciaPlantillaDetalle> detalles) {
        return sumarNutriente(detalles, "calorias");
    }

    @Named("calcularProteinas")
    default BigDecimal calcularProteinas(List<SugerenciaPlantillaDetalle> detalles) {
        return sumarNutriente(detalles, "proteinas");
    }

    @Named("calcularCarbos")
    default BigDecimal calcularCarbos(List<SugerenciaPlantillaDetalle> detalles) {
        return sumarNutriente(detalles, "carbos");
    }

    @Named("calcularGrasas")
    default BigDecimal calcularGrasas(List<SugerenciaPlantillaDetalle> detalles) {
        return sumarNutriente(detalles, "grasas");
    }

    default BigDecimal sumarNutriente(List<SugerenciaPlantillaDetalle> detalles, String tipo) {
        if (detalles == null) return BigDecimal.ZERO;

        BigDecimal total = BigDecimal.ZERO;
        for (SugerenciaPlantillaDetalle d : detalles) {
            BigDecimal factor = d.getCantidad()
                    .divide(d.getAlimento().getPorcionBase(), 4, RoundingMode.HALF_UP);

            BigDecimal valorBase = switch (tipo) {
                case "calorias"  -> d.getAlimento().getCalorias();
                case "proteinas" -> d.getAlimento().getProteina();
                case "carbos"    -> d.getAlimento().getCarbohidratos();
                case "grasas"    -> d.getAlimento().getGrasas();
                default          -> BigDecimal.ZERO;
            };

            total = total.add(valorBase.multiply(factor));
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}

