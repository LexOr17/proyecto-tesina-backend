package com.upemor.app_nutriologo.plantillas.mapper;

import com.upemor.app_nutriologo.alimento.mapper.AlimentoMapper;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaRequestDTO;
import com.upemor.app_nutriologo.plantillas.dto.PlantillaResponseDTO;
import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AlimentoMapper.class})
public interface PlantillaMapper {

    // Mapeo de Entidad a DTO de Respuesta
    // MapStruct usará automáticamente AlimentoMapper para convertir la lista de alimentos.
    PlantillaResponseDTO toDto(Plantilla plantilla);

    // Mapeo de DTO de Petición a Entidad
    @Mapping(target = "id", ignore = true) // Ignoramos el ID en la creación
    @Mapping(target = "fechaCreacion", ignore = true) // Se genera automáticamente
    @Mapping(target = "usuario", ignore = true) // Lo asignaremos manualmente en el servicio
    @Mapping(target = "alimentos", ignore = true) // Lo asignaremos manualmente en el servicio
    Plantilla toEntity(PlantillaRequestDTO dto);
}
