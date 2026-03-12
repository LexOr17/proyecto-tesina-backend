package com.upemor.app_nutriologo.citas.mapper;

import com.upemor.app_nutriologo.citas.dto.CitaRequestDTO;
import com.upemor.app_nutriologo.citas.dto.CitaResponseDTO;
import com.upemor.app_nutriologo.citas.dto.CitaSolicitudDTO;
import com.upemor.app_nutriologo.citas.entity.Cita;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CitaMapper {

    @Mappings({
            @Mapping(source = "paciente.id", target = "pacienteId"),
            @Mapping(source = "paciente", target = "nombrePaciente", qualifiedByName = "mapNombreCompleto"),
            @Mapping(source = "nutriologo.id", target = "nutriologoId"),
            @Mapping(source = "nutriologo", target = "nombreNutriologo", qualifiedByName = "mapNombreCompleto"),
            @Mapping(source = "fechaHoraReprogramacionSolicitada", target = "fechaHoraReprogramacionSolicitada")
    })
    CitaResponseDTO toResponseDTO(Cita cita);

    List<CitaResponseDTO> toResponseDTOList(List<Cita> citas);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "estado", ignore = true),
            @Mapping(target = "fechaHoraReprogramacionSolicitada", ignore = true),
            @Mapping(source = "paciente", target = "paciente"),
            @Mapping(source = "nutriologo", target = "nutriologo")
    })
    Cita toEntity(CitaRequestDTO dto, Paciente paciente, Nutriologo nutriologo);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "estado", ignore = true),
            @Mapping(target = "fechaHoraReprogramacionSolicitada", ignore = true),
            @Mapping(target = "paciente", ignore = true),
            @Mapping(target = "nutriologo", ignore = true)
    })
    void updateEntityFromDto(CitaRequestDTO dto, @MappingTarget Cita cita);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "estado", ignore = true),
            @Mapping(target = "observaciones", ignore = true),
            @Mapping(target = "fechaHoraFin", ignore = true),
            @Mapping(target = "fechaHoraReprogramacionSolicitada", ignore = true),
            @Mapping(source = "paciente", target = "paciente"),
            @Mapping(source = "nutriologo", target = "nutriologo")
    })
    Cita toEntityFromSolicitud(CitaSolicitudDTO dto, Paciente paciente, Nutriologo nutriologo);

    @Named("mapNombreCompleto")
    default String mapNombreCompleto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return usuario.getNombre() + " " + usuario.getApellidoPaterno() + 
               (usuario.getApellidoMaterno() != null ? " " + usuario.getApellidoMaterno() : "");
    }
}
