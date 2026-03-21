package com.upemor.app_nutriologo.diario_extendido.mapper;

import com.upemor.app_nutriologo.diario_extendido.dto.BebidaConsumidaDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoPacienteRequestDTO;
import com.upemor.app_nutriologo.diario_extendido.dto.DiarioExtendidoResponseDTO;
import com.upemor.app_nutriologo.diario_extendido.entity.BebidaConsumida;
import com.upemor.app_nutriologo.diario_extendido.entity.DiarioExtendido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DiarioExtendidoMapper {

    // --- De DTO a Entidad ---

    BebidaConsumida toEntity(BebidaConsumidaDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true) // El paciente se asigna en el Service
    @Mapping(target = "bebidas", ignore = true)  // Ignoramos para manejar la relación bidireccional manualmente
    @Mapping(target = "actividadFisicaSugerida", ignore = true)
    @Mapping(target = "observacionesNutriologo", ignore = true)
    DiarioExtendido toEntity(DiarioExtendidoPacienteRequestDTO dto);

    // --- De Entidad a DTO ---

    BebidaConsumidaDTO toDto(BebidaConsumida entity);

    @Mapping(source = "paciente.id", target = "pacienteId")
    DiarioExtendidoResponseDTO toResponseDto(DiarioExtendido entity);

    // --- Actualización de Entidad existente ---

    @Mapping(target = "id", ignore = true) // Nunca sobreescribir el ID en una actualización
    @Mapping(target = "paciente", ignore = true) // Nunca cambiar el paciente en una actualización
    @Mapping(target = "bebidas", ignore = true) // FUNDAMENTAL: Evita conflictos de colecciones en Hibernate
    @Mapping(target = "actividadFisicaSugerida", ignore = true)
    @Mapping(target = "observacionesNutriologo", ignore = true)
    void updateEntityFromDto(DiarioExtendidoPacienteRequestDTO dto, @MappingTarget DiarioExtendido entity);
}