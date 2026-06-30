package com.upemor.app_nutriologo.plan_alimenticio.mapper;

import com.upemor.app_nutriologo.plan_alimenticio.dto.*;
import com.upemor.app_nutriologo.plan_alimenticio.entity.ComidaProgramada;
import com.upemor.app_nutriologo.plan_alimenticio.entity.PlanAlimenticio;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PlanAlimenticioMapper {

    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "nutriologo.id", target = "nutriologoId")
    PlanAlimenticioResponseDTO toResponseDto(PlanAlimenticio entity);

    @Mapping(source = "plantilla.id", target = "plantillaId")
    @Mapping(source = "plantilla.nombre", target = "plantillaNombre")
    ComidaProgramadaResponseDTO toResponseDto(ComidaProgramada entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "nutriologo", ignore = true)
    @Mapping(target = "comidasProgramadas", ignore = true)
    PlanAlimenticio toEntity(PlanAlimenticioRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "planAlimenticio", ignore = true)
    @Mapping(target = "plantilla", ignore = true)
    ComidaProgramada toEntity(ComidaProgramadaDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "nutriologo", ignore = true)
    @Mapping(target = "comidasProgramadas", ignore = true)
    void updateEntityFromDto(PlanAlimenticioRequestDTO dto, @MappingTarget PlanAlimenticio entity);
}