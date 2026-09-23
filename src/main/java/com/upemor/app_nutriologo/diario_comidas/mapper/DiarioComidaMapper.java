package com.upemor.app_nutriologo.diario_comidas.mapper;

import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaRequestDTO;
import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaResponseDTO;
import com.upemor.app_nutriologo.diario_comidas.entity.DiarioComida;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper MapStruct para la conversión entre la entidad {@link DiarioComida} y sus respectivos DTOs.
 * <p>
 * Facilita la transformación bidireccional entre peticiones de registro/actualización,
 * entidades persistidas y objetos de transferencia de respuesta.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface DiarioComidaMapper {

    /**
     * Convierte la entidad {@link DiarioComida} a su DTO de respuesta {@link DiarioComidaResponseDTO}.
     * Mapea el identificador de la comida programada y el nombre de la plantilla de sugerencia asociada.
     *
     * @param entity Entidad de diario de comida a convertir.
     * @return DTO de respuesta con la información detallada del registro.
     */
    @Mapping(source = "comidaProgramada.id", target = "comidaProgramadaId")
    @Mapping(source = "comidaProgramada.plantilla.nombre", target = "nombreComidaSugerida")
    DiarioComidaResponseDTO toResponseDto(DiarioComida entity);

    /**
     * Convierte el DTO de petición {@link DiarioComidaRequestDTO} a la entidad {@link DiarioComida}.
     *
     * @param dto DTO con los datos del registro de comida reportado por el paciente.
     * @return Entidad {@link DiarioComida} inicializada.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "comidaProgramada", ignore = true)
    @Mapping(target = "fechaRegistroSistema", ignore = true)
    @Mapping(target = "observacionNutriologo", ignore = true)
    DiarioComida toEntity(DiarioComidaRequestDTO dto);

    /**
     * Mapea una lista de entidades {@link DiarioComida} a una lista de {@link DiarioComidaResponseDTO}.
     *
     * @param entities Lista de entidades de diario de comida.
     * @return Lista de DTOs de respuesta correspondientes.
     */
    List<DiarioComidaResponseDTO> toResponseDtoList(List<DiarioComida> entities);
}