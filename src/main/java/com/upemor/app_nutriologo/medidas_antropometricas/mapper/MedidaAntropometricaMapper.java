package com.upemor.app_nutriologo.medidas_antropometricas.mapper;

import com.upemor.app_nutriologo.medidas_antropometricas.dto.MedidaAntropometricaRequestDTO;
import com.upemor.app_nutriologo.medidas_antropometricas.dto.MedidaAntropometricaResponseDTO;
import com.upemor.app_nutriologo.medidas_antropometricas.entity.MedidaAntropometrica;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MedidaAntropometricaMapper {

    MedidaAntropometricaResponseDTO toResponseDto(MedidaAntropometrica medida);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaMedicion", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    MedidaAntropometrica toEntity(MedidaAntropometricaRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaMedicion", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    void updateEntityFromDto(MedidaAntropometricaRequestDTO dto, @MappingTarget MedidaAntropometrica entity);
}
