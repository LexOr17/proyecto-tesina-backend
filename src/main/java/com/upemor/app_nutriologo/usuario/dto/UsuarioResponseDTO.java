package com.upemor.app_nutriologo.usuario.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.upemor.app_nutriologo.administrador.dto.AdministradorResponseDTO;
import com.upemor.app_nutriologo.nutriologo.dto.NutriologoResponseDTO;
import com.upemor.app_nutriologo.paciente.dto.PacienteResponseDTO;
import com.upemor.app_nutriologo.usuario.enums.Genero;
import com.upemor.app_nutriologo.usuario.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "rol",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AdministradorResponseDTO.class, name = "ADMINISTRADOR"),
        @JsonSubTypes.Type(value = NutriologoResponseDTO.class, name = "NUTRIOLOGO"),
        @JsonSubTypes.Type(value = PacienteResponseDTO.class, name = "PACIENTE")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class UsuarioResponseDTO {

    private Long id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Genero genero;
    private LocalDate fechaNacimiento;
    private String correo;
    private String telefono;
    private Rol rol;
    private Boolean activo;

}
