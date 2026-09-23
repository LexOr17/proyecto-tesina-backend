package com.upemor.app_nutriologo.seguimiento.mapper;

import com.upemor.app_nutriologo.seguimiento.dto.SeguimientoConsultaDTO;
import com.upemor.app_nutriologo.seguimiento.entity.SeguimientoConsulta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeguimientoMapper {

    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(target = "pacienteNombre", expression = "java(entity.getPaciente() != null ? entity.getPaciente().getNombre() + \" \" + entity.getPaciente().getApellidoPaterno() : null)")
    @Mapping(source = "nutriologo.id", target = "nutriologoId")
    @Mapping(target = "nutriologoNombre", expression = "java(entity.getNutriologo() != null ? entity.getNutriologo().getNombre() + \" \" + entity.getNutriologo().getApellidoPaterno() : null)")
    SeguimientoConsultaDTO toDto(SeguimientoConsulta entity);
}
