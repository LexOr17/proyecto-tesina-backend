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

/**
 * Mapper MapStruct para la conversión bidireccional entre entidades de usuario y sus correspondientes DTOs.
 * <p>
 * Maneja la jerarquía polimórfica de usuarios despachando dinámicamente según el tipo concreto
 * ({@link Paciente}, {@link Nutriologo}, {@link Administrador}) hacia y desde sus respectivos DTOs
 * de solicitud y respuesta.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    /**
     * Convierte polimórficamente una entidad concreta de {@link Usuario} a su correspondiente {@link UsuarioResponseDTO}.
     *
     * @param usuario entidad de usuario a transformar.
     * @return instancia especializada de {@link UsuarioResponseDTO} según el subtipo de la entidad.
     * @throws IllegalArgumentException si el tipo concreto de la entidad no es reconocido.
     */
    default UsuarioResponseDTO toResponseDto(Usuario usuario) {
        if (usuario instanceof Nutriologo n)    return toNutriologoResponseDto(n);
        if (usuario instanceof Paciente p)      return toPacienteResponseDto(p);
        if (usuario instanceof Administrador a) return toAdminResponseDto(a);
        throw new IllegalArgumentException("Tipo de entidad desconocido: " + usuario.getClass().getName());
    }

    /**
     * Convierte polimórficamente un {@link UsuarioRequestDTO} a su correspondiente entidad especializada de {@link Usuario}.
     *
     * @param dto DTO de solicitud con los datos del usuario.
     * @return entidad concreta de {@link Usuario} (como {@link Nutriologo}, {@link Paciente} o {@link Administrador}).
     * @throws IllegalArgumentException si el subtipo del DTO no es reconocido.
     */
    default Usuario toEntity(UsuarioRequestDTO dto) {
        if (dto instanceof NutriologoRequestDTO n)    return toNutriologoEntity(n);
        if (dto instanceof PacienteRequestDTO p)      return toPacienteEntity(p);
        if (dto instanceof AdministradorRequestDTO a) return toAdminEntity(a);
        throw new IllegalArgumentException("Tipo de DTO desconocido: " + dto.getClass().getName());
    }

    /**
     * Mapea una entidad {@link Nutriologo} a su representación de respuesta {@link NutriologoResponseDTO}.
     *
     * @param nutriologo entidad del nutriólogo.
     * @return DTO con la información de respuesta del nutriólogo.
     */
    NutriologoResponseDTO toNutriologoResponseDto(Nutriologo nutriologo);

    /**
     * Mapea un DTO de solicitud {@link NutriologoRequestDTO} a su correspondiente entidad {@link Nutriologo}.
     * <p>
     * Se ignoran atributos gestionados por persistencia o colecciones bidireccionales.
     * </p>
     *
     * @param dto DTO con los datos de registro del nutriólogo.
     * @return entidad {@link Nutriologo} inicializada.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "pacientes", ignore = true)
    @Mapping(target = "citas", ignore = true)
    Nutriologo toNutriologoEntity(NutriologoRequestDTO dto);

    /**
     * Mapea una entidad {@link Paciente} a su representación de respuesta {@link PacienteResponseDTO},
     * aplanando los datos del nutriólogo asignado.
     *
     * @param paciente entidad del paciente.
     * @return DTO con los datos de respuesta del paciente.
     */
    @Mapping(target = "nutriologoId", source = "nutriologo.id")
    @Mapping(target = "nombreNutriologo", source = "nutriologo.nombre")
    @Mapping(target = "apellidoPaternoNutriologo", source = "nutriologo.apellidoPaterno")
    PacienteResponseDTO toPacienteResponseDto(Paciente paciente);

    /**
     * Mapea un DTO de solicitud {@link PacienteRequestDTO} a su correspondiente entidad {@link Paciente},
     * asociando el identificador del nutriólogo y omitiendo colecciones dependientes.
     *
     * @param dto DTO con los datos del paciente.
     * @return entidad {@link Paciente} configurada.
     */
    @Mapping(target = "nutriologo.id", source = "nutriologoId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "medidasAntropometricas", ignore = true)
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "metas", ignore = true)
    @Mapping(target = "historialesMedicos", ignore = true)
    @Mapping(target = "diariosComida", ignore = true)
    @Mapping(target = "diariosExtendidos", ignore = true)
    @Mapping(target = "planesAlimenticios", ignore = true)
    @Mapping(target = "sugerenciasPlantilla", ignore = true)
    Paciente toPacienteEntity(PacienteRequestDTO dto);

    /**
     * Mapea una entidad {@link Administrador} a su representación de respuesta {@link AdministradorResponseDTO}.
     *
     * @param administrador entidad del administrador.
     * @return DTO de respuesta del administrador.
     */
    AdministradorResponseDTO toAdminResponseDto(Administrador administrador);

    /**
     * Mapea un DTO de solicitud {@link AdministradorRequestDTO} a su correspondiente entidad {@link Administrador}.
     *
     * @param dto DTO con los datos del administrador.
     * @return entidad {@link Administrador} resultante.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "activo", ignore = true)
    Administrador toAdminEntity(AdministradorRequestDTO dto);
}
