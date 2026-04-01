package com.upemor.app_nutriologo.plantillas.mapper;

import com.upemor.app_nutriologo.plantillas.dto.*;
import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import com.upemor.app_nutriologo.plantillas.entity.PlantillaAlimento;
import com.upemor.app_nutriologo.utils.NutrienteCalculator;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PlantillaMapper {

    @Mapping(target = "totalCalorias",      source = "detalles", qualifiedByName = "calcularCalorias")
    @Mapping(target = "totalProteinas",     source = "detalles", qualifiedByName = "calcularProteinas")
    @Mapping(target = "totalCarbohidratos", source = "detalles", qualifiedByName = "calcularCarbos")
    @Mapping(target = "totalGrasas",        source = "detalles", qualifiedByName = "calcularGrasas")
    PlantillaResponseDTO toDto(Plantilla plantilla);

    @Mapping(source = "alimento.id",           target = "alimentoId")
    @Mapping(source = "alimento.nombre",       target = "nombreAlimento")
    @Mapping(source = "alimento.unidadPorcion", target = "unidadPorcion")
    PlantillaAlimentoResponseDTO toAlimentoDto(PlantillaAlimento detalle);

    @Mapping(target = "id",            ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "usuario",       ignore = true)
    @Mapping(target = "detalles",      ignore = true)
    Plantilla toEntity(PlantillaRequestDTO dto);

    // --- Cálculos nutricionales delegados a NutrienteCalculator ---

    @Named("calcularCalorias")
    default BigDecimal calcularCalorias(List<PlantillaAlimento> detalles) {
        return NutrienteCalculator.sumar(detalles,
                PlantillaAlimento::getCantidad,
                d -> d.getAlimento().getPorcionBase(),
                d -> d.getAlimento().getCalorias());
    }

    @Named("calcularProteinas")
    default BigDecimal calcularProteinas(List<PlantillaAlimento> detalles) {
        return NutrienteCalculator.sumar(detalles,
                PlantillaAlimento::getCantidad,
                d -> d.getAlimento().getPorcionBase(),
                d -> d.getAlimento().getProteina());
    }

    @Named("calcularCarbos")
    default BigDecimal calcularCarbos(List<PlantillaAlimento> detalles) {
        return NutrienteCalculator.sumar(detalles,
                PlantillaAlimento::getCantidad,
                d -> d.getAlimento().getPorcionBase(),
                d -> d.getAlimento().getCarbohidratos());
    }

    @Named("calcularGrasas")
    default BigDecimal calcularGrasas(List<PlantillaAlimento> detalles) {
        return NutrienteCalculator.sumar(detalles,
                PlantillaAlimento::getCantidad,
                d -> d.getAlimento().getPorcionBase(),
                d -> d.getAlimento().getGrasas());
    }
}