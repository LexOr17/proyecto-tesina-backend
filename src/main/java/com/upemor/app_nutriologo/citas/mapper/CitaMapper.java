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
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CitaMapper {

    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "paciente", target = "nombrePaciente", qualifiedByName = "mapNombreCompleto")
    @Mapping(source = "nutriologo.id", target = "nutriologoId")
    @Mapping(source = "nutriologo", target = "nombreNutriologo", qualifiedByName = "mapNombreCompleto")
    @Mapping(source = "fechaHoraReprogramacionSolicitada", target = "fechaHoraReprogramacionSolicitada")
    CitaResponseDTO toResponseDTO(Cita cita);

    List<CitaResponseDTO> toResponseDTOList(List<Cita> citas);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaHoraReprogramacionSolicitada", ignore = true)
    @Mapping(target = "observaciones", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    @Mapping(source = "paciente", target = "paciente")
    @Mapping(source = "nutriologo", target = "nutriologo")
    Cita toEntity(CitaRequestDTO dto, Paciente paciente, Nutriologo nutriologo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaHoraReprogramacionSolicitada", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "nutriologo", ignore = true)
    @Mapping(target = "observaciones", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    void updateEntityFromDto(CitaRequestDTO dto, @MappingTarget Cita cita);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true) // Se asigna en el Service
    @Mapping(target = "fechaHoraFin", ignore = true) // Se calcula en el Service
    @Mapping(target = "fechaHoraReprogramacionSolicitada", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    @Mapping(target = "observaciones", ignore = true)
    @Mapping(source = "paciente", target = "paciente")
    @Mapping(source = "nutriologo", target = "nutriologo")
    @Mapping(source = "dto.fechaHoraInicio", target = "fechaHoraInicio")
    @Mapping(source = "dto.motivoCita", target = "motivoCita")
    @Mapping(source = "dto.detalleAdicional", target = "detalleAdicional")
    Cita toEntity(CitaSolicitudDTO dto, Paciente paciente, Nutriologo nutriologo);


    @Named("mapNombreCompleto")
    default String mapNombreCompleto(Usuario usuario) {
        if (usuario == null) return null;
        return usuario.getNombre() + " " + usuario.getApellidoPaterno() +
               (usuario.getApellidoMaterno() != null ? " " + usuario.getApellidoMaterno() : "");
    }
}
