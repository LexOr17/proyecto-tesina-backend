package com.upemor.app_nutriologo.plan_alimenticio.mapper;
import com.upemor.app_nutriologo.plan_alimenticio.dtos.ComidaProgramadaDTO;
import com.upemor.app_nutriologo.plan_alimenticio.dtos.ComidaProgramadaResponseDTO;
import com.upemor.app_nutriologo.plan_alimenticio.dtos.PlanAlimenticioRequestDTO;
import com.upemor.app_nutriologo.plan_alimenticio.dtos.PlanAlimenticioResponseDTO;
import com.upemor.app_nutriologo.plan_alimenticio.entity.ComidaProgramada;
import com.upemor.app_nutriologo.plan_alimenticio.entity.PlanAlimenticio;
import org.mapstruct.*;

@Mapper(componentModel = "spring") // Le dice a Spring que lo inyecte como un @Service
public interface PlanAlimenticioMapper {

    // ==========================================
    // ENTITY -> RESPONSE DTO (Salida hacia el Frontend)
    // ==========================================
    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "nutriologo.id", target = "nutriologoId")
    PlanAlimenticioResponseDTO toResponseDto(PlanAlimenticio entity);

    @Mapping(source = "plantilla.id", target = "plantillaId")
    @Mapping(source = "plantilla.nombre", target = "plantillaNombre")
    ComidaProgramadaResponseDTO toResponseDto(ComidaProgramada entity);

    // ==========================================
    // REQUEST DTO -> ENTITY (Entrada desde el Frontend)
    // ==========================================
    // Ignoramos paciente, nutriologo y las comidas porque las setearemos
    // manualmente en el Service, ya que requieren ir a la base de datos a buscarse.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "nutriologo", ignore = true)
    @Mapping(target = "comidasProgramadas", ignore = true)
    PlanAlimenticio toEntity(PlanAlimenticioRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "planAlimenticio", ignore = true)
    @Mapping(target = "plantilla", ignore = true) // Se busca en la BD en el Service
    ComidaProgramada toEntity(ComidaProgramadaDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "nutriologo", ignore = true)
    @Mapping(target = "comidasProgramadas", ignore = true)
    void updateEntityFromDto(PlanAlimenticioRequestDTO dto, @MappingTarget PlanAlimenticio entity);
}