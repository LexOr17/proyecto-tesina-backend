package com.upemor.app_nutriologo.alimento.mapper;

import com.upemor.app_nutriologo.alimento.dto.AlimentoRequestDTO;
import com.upemor.app_nutriologo.alimento.dto.AlimentoResponseDTO;
import com.upemor.app_nutriologo.alimento.entity.Alimento;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Mapper(componentModel = "spring")
public abstract class AlimentoMapper {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Mapping(target = "registradoPor", source = "registradoPor", qualifiedByName = "mapRegistradoPor")
    @Mapping(target = "imagenUrl", source = "imagenUrl", qualifiedByName = "mapImagenUrl")
    public abstract AlimentoResponseDTO toResponseDto(Alimento alimento);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "registradoPor", ignore = true)
    @Mapping(target = "imagenUrl", ignore = true) // Se maneja manualmente en el servicio
    public abstract Alimento toEntity(AlimentoRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "registradoPor", ignore = true)
    @Mapping(target = "imagenUrl", ignore = true) // Se maneja manualmente en el servicio
    public abstract void updateEntityFromDto(AlimentoRequestDTO dto, @MappingTarget Alimento entity);

    @Named("mapRegistradoPor")
    protected String mapRegistradoPor(Usuario usuario) {
        if (usuario == null) {
            return "Desconocido";
        }
        return usuario.getCorreo();
    }

    @Named("mapImagenUrl")
    protected String mapImagenUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        // Construye la URL completa: http://localhost:8080/uploads/nombre-archivo.jpg
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(fileName)
                .toUriString();
    }
}
