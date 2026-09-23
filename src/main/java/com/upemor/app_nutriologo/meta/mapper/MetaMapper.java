package com.upemor.app_nutriologo.meta.mapper;

import com.upemor.app_nutriologo.meta.dto.MetaRequestDTO;
import com.upemor.app_nutriologo.meta.dto.MetaResponseDTO;
import com.upemor.app_nutriologo.meta.entity.Meta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
public interface MetaMapper {

    // --- De DTO a Entidad (Creación) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true) // Se asigna PENDIENTE por defecto en la entidad
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "nutriologo", ignore = true)
    @Mapping(target = "observaciones", ignore = true)
    Meta toEntity(MetaRequestDTO dto);

    // --- De Entidad a DTO (Lectura) ---
    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "nutriologo.id", target = "nutriologoId")
    MetaResponseDTO toResponseDto(Meta meta);

    // --- De DTO a Entidad (Actualización de texto/fecha) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true) // No permitimos cambiar el estado desde el DTO de edición
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "nutriologo", ignore = true)
    @Mapping(target = "observaciones", ignore = true)
    void updateEntityFromDto(MetaRequestDTO dto, @MappingTarget Meta entity);
}