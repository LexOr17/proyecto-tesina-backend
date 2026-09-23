package com.upemor.app_nutriologo.diario_extendido.mapper;

import com.upemor.app_nutriologo.diario_extendido.dto.BebidaConsumidaDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoNutriologoRequestDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoPacienteRequestDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoResponseDTO;
import com.upemor.app_nutriologo.diario_extendido.entity.BebidaConsumida;
import com.upemor.app_nutriologo.diario_extendido.entity.DiarioExtendido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DiarioExtendidoMapper {

    // --- Mapeos Básicos ---
    @Mapping(target = "diarioExtendido", ignore = true)
    BebidaConsumida toEntity(BebidaConsumidaDTO dto);
    BebidaConsumidaDTO toDto(BebidaConsumida entity);

    @Mapping(source = "paciente.id", target = "pacienteId")
    DiarioExtendidoResponseDTO toResponseDto(DiarioExtendido entity);

    // --- Actualización desde el PACIENTE ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    @Mapping(target = "bebidas", ignore = true) // Se maneja manualmente en el Service
    @Mapping(target = "tipoActividadSugerida", ignore = true) // El paciente no edita esto
    @Mapping(target = "actividadDetalle", ignore = true)
    @Mapping(target = "observacionesNutriologo", ignore = true)
    void updateEntityFromDto(DiarioExtendidoPacienteRequestDTO dto, @MappingTarget DiarioExtendido entity);

    // --- Actualización desde el NUTRIÓLOGO ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    @Mapping(target = "estadoAnimo", ignore = true) // El nutriólogo no edita esto
    @Mapping(target = "calidadSueno", ignore = true)
    @Mapping(target = "sintomaPrincipal", ignore = true)
    @Mapping(target = "sintomaDetalle", ignore = true)
    @Mapping(target = "bebidas", ignore = true)
    @Mapping(target = "actividadRealizada", ignore = true)
    void updateEntityFromNutriologoDto(DiarioExtendidoNutriologoRequestDTO dto, @MappingTarget DiarioExtendido entity);
}