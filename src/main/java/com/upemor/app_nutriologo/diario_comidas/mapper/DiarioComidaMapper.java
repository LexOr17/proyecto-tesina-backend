package com.upemor.app_nutriologo.diario_comidas.mapper;

import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaRequestDTO;
import com.upemor.app_nutriologo.diario_comidas.dtos.DiarioComidaResponseDTO;
import com.upemor.app_nutriologo.diario_comidas.entity.DiarioComida;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper para convertir entre la entidad DiarioComida y sus respectivos Records (DTOs).
 * MapStruct 1.5+ soporta Records de forma nativa.
 */
@Mapper(componentModel = "spring")
public interface DiarioComidaMapper {

    /**
     * Convierte la Entidad a un Record de Respuesta.
     * Mapeamos el ID de la comida programada y el nombre de la plantilla sugerida.
     */
    @Mapping(source = "comidaProgramada.id", target = "comidaProgramadaId")
    @Mapping(source = "comidaProgramada.plantilla.nombre", target = "nombreComidaSugerida")
    DiarioComidaResponseDTO toResponseDto(DiarioComida entity);

    /**
     * Convierte el Record de Petición a una Entidad.
     * Ignoramos el ID, el paciente, la relación de comidaProgramada y la fecha del sistema,
     * ya que estos se asignan en la lógica de negocio del Service.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "comidaProgramada", ignore = true)
    @Mapping(target = "fechaRegistroSistema", ignore = true)
    @Mapping(target = "observacionNutriologo", ignore = true)
    DiarioComida toEntity(DiarioComidaRequestDTO dto);

    /**
     * Mapeo de listas para reportes históricos o semanales.
     */
    List<DiarioComidaResponseDTO> toResponseDtoList(List<DiarioComida> entities);
}