package com.upemor.app_nutriologo.alimento.mapper;

import com.upemor.app_nutriologo.alimento.dto.AlimentoRequestDTO;
import com.upemor.app_nutriologo.alimento.dto.AlimentoResponseDTO;
import com.upemor.app_nutriologo.alimento.entity.Alimento;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Mapper MapStruct para la transformación bidireccional entre la entidad {@link Alimento} y sus DTOs asociados.
 * <p>
 * Facilita el mapeo de atributos nutrimentales, la resolución dinámica de URLs de imágenes estáticas
 * y la proyección del usuario que registró el alimento.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface AlimentoMapper {

    /**
     * Mapea una entidad {@link Alimento} a su correspondiente record de respuesta {@link AlimentoResponseDTO}.
     *
     * @param alimento entidad del alimento a transformar.
     * @return {@link AlimentoResponseDTO} proyectado con URL completa y datos de usuario.
     */
    @Mapping(target = "registradoPor", source = "registradoPor", qualifiedByName = "mapRegistradoPor")
    @Mapping(target = "imagenUrl", source = "imagenUrl", qualifiedByName = "mapImagenUrl")
    AlimentoResponseDTO toResponseDto(Alimento alimento);

    /**
     * Convierte un {@link AlimentoRequestDTO} a una nueva entidad {@link Alimento}.
     *
     * @param dto DTO con los datos de creación del alimento.
     * @return entidad {@link Alimento} inicializada (omitiendo id, fecha de registro y usuario).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "registradoPor", ignore = true)
    @Mapping(target = "imagenUrl", ignore = true) // Se maneja manualmente en el servicio
    Alimento toEntity(AlimentoRequestDTO dto);

    /**
     * Actualiza el estado de una entidad {@link Alimento} existente a partir de los datos provistos en {@link AlimentoRequestDTO}.
     *
     * @param dto DTO con los valores modificados del alimento.
     * @param entity entidad {@link Alimento} destino a actualizar in-situ.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "registradoPor", ignore = true)
    @Mapping(target = "imagenUrl", ignore = true) // Se maneja manualmente en el servicio
    void updateEntityFromDto(AlimentoRequestDTO dto, @MappingTarget Alimento entity);

    /**
     * Método calificado para resolver el correo electrónico del usuario creador o una cadena por defecto.
     *
     * @param usuario entidad del usuario registrado.
     * @return correo del usuario o {@code "Desconocido"} si es nulo.
     */
    @Named("mapRegistradoPor")
    default String mapRegistradoPor(Usuario usuario) {
        return usuario == null ? "Desconocido" : usuario.getCorreo();
    }

    /**
     * Método calificado para construir la URL pública absoluta del recurso gráfico a partir de su nombre de archivo.
     *
     * @param fileName nombre o identificador del archivo de imagen.
     * @return URI pública completa para acceder a la imagen o {@code null} si no está definida.
     */
    @Named("mapImagenUrl")
    default String mapImagenUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) return null;
        // Construye la URL completa: http://localhost:8080/uploads/nombre-archivo.jpg
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(fileName)
                .toUriString();
    }
}
