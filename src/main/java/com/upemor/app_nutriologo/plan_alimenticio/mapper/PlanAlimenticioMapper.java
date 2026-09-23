package com.upemor.app_nutriologo.plan_alimenticio.mapper;

import com.upemor.app_nutriologo.plan_alimenticio.dto.*;
import com.upemor.app_nutriologo.plan_alimenticio.entity.ComidaProgramada;
import com.upemor.app_nutriologo.plan_alimenticio.entity.PlanAlimenticio;
import org.mapstruct.*;

/**
 * Mapper MapStruct para la conversión bidireccional entre las entidades del plan alimenticio y sus respectivos DTOs.
 * <p>
 * Transforma instancias de {@link PlanAlimenticio} y {@link ComidaProgramada} aplanando los identificadores de
 * relaciones y gestionando actualizaciones in-situ.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface PlanAlimenticioMapper {

    /**
     * Mapea una entidad {@link PlanAlimenticio} a su DTO de respuesta {@link PlanAlimenticioResponseDTO}.
     *
     * @param entity entidad del plan alimenticio.
     * @return {@link PlanAlimenticioResponseDTO} con los datos y comidas del plan.
     */
    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "nutriologo.id", target = "nutriologoId")
    PlanAlimenticioResponseDTO toResponseDto(PlanAlimenticio entity);

    /**
     * Mapea una entidad {@link ComidaProgramada} a su DTO de respuesta {@link ComidaProgramadaResponseDTO}.
     *
     * @param entity entidad de la comida programada.
     * @return {@link ComidaProgramadaResponseDTO} con los datos de la comida y la plantilla vinculada.
     */
    @Mapping(source = "plantilla.id", target = "plantillaId")
    @Mapping(source = "plantilla.nombre", target = "plantillaNombre")
    ComidaProgramadaResponseDTO toResponseDto(ComidaProgramada entity);

    /**
     * Convierte un {@link PlanAlimenticioRequestDTO} a una entidad {@link PlanAlimenticio}.
     *
     * @param dto DTO de solicitud con los datos del plan.
     * @return entidad {@link PlanAlimenticio} inicializada.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "nutriologo", ignore = true)
    @Mapping(target = "comidasProgramadas", ignore = true)
    PlanAlimenticio toEntity(PlanAlimenticioRequestDTO dto);

    /**
     * Convierte un {@link ComidaProgramadaDTO} a una entidad {@link ComidaProgramada}.
     *
     * @param dto DTO con los datos de la comida a calendarizar.
     * @return entidad {@link ComidaProgramada} resultante.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "planAlimenticio", ignore = true)
    @Mapping(target = "plantilla", ignore = true)
    ComidaProgramada toEntity(ComidaProgramadaDTO dto);

    /**
     * Actualiza una entidad {@link PlanAlimenticio} existente a partir de los datos provistos en {@link PlanAlimenticioRequestDTO}.
     *
     * @param dto DTO con los datos actualizados del plan.
     * @param entity entidad destino a actualizar.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "nutriologo", ignore = true)
    @Mapping(target = "comidasProgramadas", ignore = true)
    void updateEntityFromDto(PlanAlimenticioRequestDTO dto, @MappingTarget PlanAlimenticio entity);
}