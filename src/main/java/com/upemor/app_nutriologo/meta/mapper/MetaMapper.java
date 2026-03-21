package com.upemor.app_nutriologo.meta.mapper;

import com.upemor.app_nutriologo.meta.dto.MetaRequestDTO;
import com.upemor.app_nutriologo.meta.dto.MetaResponseDTO;
import com.upemor.app_nutriologo.meta.entity.Meta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MetaMapper {

    @Mapping(target = "paciente.id", source = "pacienteId")
    Meta toEntity(MetaRequestDTO dto);

    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "nutriologo.id", target = "nutriologoId")
    MetaResponseDTO toResponseDto(Meta meta);
}
