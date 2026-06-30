package com.upemor.app_nutriologo.sugerencia_plantilla.mapper;

import com.upemor.app_nutriologo.plantillas.dto.PlantillaAlimentoResponseDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.dto.SugerenciaPlantillaResponseDTO;
import com.upemor.app_nutriologo.sugerencia_plantilla.entity.SugerenciaPlantilla;
import com.upemor.app_nutriologo.sugerencia_plantilla.entity.SugerenciaPlantillaDetalle;
import com.upemor.app_nutriologo.utils.NutrienteCalculator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SugerenciaPlantillaMapper {

    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "nutriologo.id", target = "nutriologoId")
    @Mapping(source = "detalles", target = "totalCalorias", qualifiedByName = "calcularCalorias")
    @Mapping(source = "detalles", target = "totalProteinas", qualifiedByName = "calcularProteinas")
    @Mapping(source = "detalles", target = "totalCarbohidratos", qualifiedByName = "calcularCarbos")
    @Mapping(source = "detalles", target = "totalGrasas", qualifiedByName = "calcularGrasas")
    SugerenciaPlantillaResponseDTO toDto(SugerenciaPlantilla sugerencia);

    @Mapping(source = "alimento.id", target = "alimentoId")
    @Mapping(source = "alimento.nombre", target = "nombreAlimento")
    @Mapping(source = "alimento.unidadPorcion", target = "unidadPorcion")
    PlantillaAlimentoResponseDTO toDetalleDto(SugerenciaPlantillaDetalle detalle);

    // --- Cálculos nutricionales delegados a NutrienteCalculator ---

    @Named("calcularCalorias")
    default BigDecimal calcularCalorias(List<SugerenciaPlantillaDetalle> detalles) {
        return NutrienteCalculator.sumar(detalles,
                d -> d.getCantidad(),
                d -> d.getAlimento().getPorcionBase(),
                d -> d.getAlimento().getCalorias());
    }

    @Named("calcularProteinas")
    default BigDecimal calcularProteinas(List<SugerenciaPlantillaDetalle> detalles) {
        return NutrienteCalculator.sumar(detalles,
                a -> a.getCantidad(),
                d -> d.getAlimento().getPorcionBase(),
                d -> d.getAlimento().getProteina());
    }

    @Named("calcularCarbos")
    default BigDecimal calcularCarbos(List<SugerenciaPlantillaDetalle> detalles) {
        return NutrienteCalculator.sumar(detalles,
                a -> a.getCantidad(),
                d -> d.getAlimento().getPorcionBase(),
                d -> d.getAlimento().getCarbohidratos());
    }

    @Named("calcularGrasas")
    default BigDecimal calcularGrasas(List<SugerenciaPlantillaDetalle> detalles) {
        return NutrienteCalculator.sumar(detalles,
                a -> a.getCantidad(),
                d -> d.getAlimento().getPorcionBase(),
                d -> d.getAlimento().getGrasas());
    }
}
