package com.upemor.app_nutriologo.meta.mapper;

import com.upemor.app_nutriologo.meta.dto.MetaRequestDTO;
import com.upemor.app_nutriologo.meta.dto.MetaResponseDTO;
import com.upemor.app_nutriologo.meta.entity.Meta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MetaMapper {

    // --- De DTO a Entidad (Creación) ---

    @Mapping(target = "id", ignore = true) // El ID lo genera la base de datos
    @Mapping(target = "cumplida", ignore = true) // Por defecto es false
    @Mapping(target = "paciente", ignore = true) // Lo asignamos manualmente en el Service por seguridad
    @Mapping(target = "nutriologo", ignore = true) // Lo sacamos del token en el Service
    Meta toEntity(MetaRequestDTO dto);

    // --- De Entidad a DTO (Lectura) ---

    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "nutriologo.id", target = "nutriologoId")
    MetaResponseDTO toResponseDto(Meta meta);

    // --- De DTO a Entidad (Actualización) ---

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cumplida", ignore = true) // No queremos que se marque como cumplida por accidente al editar el texto
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "nutriologo", ignore = true)
    void updateEntityFromDto(MetaRequestDTO dto, @MappingTarget Meta entity);
}