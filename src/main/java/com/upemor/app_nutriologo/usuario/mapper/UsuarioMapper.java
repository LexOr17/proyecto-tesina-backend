package com.upemor.app_nutriologo.usuario.mapper;

import com.upemor.app_nutriologo.administrador.dto.AdministradorRequestDTO;
import com.upemor.app_nutriologo.administrador.dto.AdministradorResponseDTO;
import com.upemor.app_nutriologo.administrador.entity.Administrador;
import com.upemor.app_nutriologo.nutriologo.dto.NutriologoRequestDTO;
import com.upemor.app_nutriologo.nutriologo.dto.NutriologoResponseDTO;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.paciente.dto.PacienteRequestDTO;
import com.upemor.app_nutriologo.paciente.dto.PacienteResponseDTO;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.usuario.dto.UsuarioRequestDTO;
import com.upemor.app_nutriologo.usuario.dto.UsuarioResponseDTO;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    // Métodos para convertir de Entidad a ResponseDTO (salida)
    default UsuarioResponseDTO toResponseDto(Usuario usuario) {
        if (usuario instanceof Nutriologo) {
            return toNutriologoResponseDto((Nutriologo) usuario);
        } else if (usuario instanceof Paciente) {
            return toPacienteResponseDto((Paciente) usuario);
        } else if (usuario instanceof Administrador) {
            return toAdminResponseDto((Administrador) usuario);
        }
        throw new IllegalArgumentException("Tipo de entidad desconocido: " + usuario.getClass().getName());
    }

    // Métodos para convertir de RequestDTO a Entidad (entrada)
    default Usuario toEntity(UsuarioRequestDTO dto) {
        if (dto instanceof NutriologoRequestDTO) {
            return toNutriologoEntity((NutriologoRequestDTO) dto);
        } else if (dto instanceof PacienteRequestDTO) {
            return toPacienteEntity((PacienteRequestDTO) dto);
        } else if (dto instanceof AdministradorRequestDTO) {
            return toAdminEntity((AdministradorRequestDTO) dto);
        }
        throw new IllegalArgumentException("Tipo de DTO desconocido: " + dto.getClass().getName());
    }

    // Mapeos específicos para cada rol
    NutriologoResponseDTO toNutriologoResponseDto(Nutriologo nutriologo);
    Nutriologo toNutriologoEntity(NutriologoRequestDTO dto);

    PacienteResponseDTO toPacienteResponseDto(Paciente paciente);

    @Mapping(target = "nutriologo.id", source = "nutriologoId")
    Paciente toPacienteEntity(PacienteRequestDTO dto);

    AdministradorResponseDTO toAdminResponseDto(Administrador administrador);
    Administrador toAdminEntity(AdministradorRequestDTO dto);
}
