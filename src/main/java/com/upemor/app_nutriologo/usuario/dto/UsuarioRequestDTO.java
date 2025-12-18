package com.upemor.app_nutriologo.usuario.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.upemor.app_nutriologo.administrador.dto.AdministradorRequestDTO;
import com.upemor.app_nutriologo.nutriologo.dto.NutriologoRequestDTO;
import com.upemor.app_nutriologo.paciente.dto.PacienteRequestDTO;
import com.upemor.app_nutriologo.usuario.enums.Genero;
import com.upemor.app_nutriologo.usuario.enums.Rol;
import jakarta.validation.constraints.*;
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
        @JsonSubTypes.Type(value = AdministradorRequestDTO.class, name = "ADMINISTRADOR"),
        @JsonSubTypes.Type(value = NutriologoRequestDTO.class, name = "NUTRIOLOGO"),
        @JsonSubTypes.Type(value = PacienteRequestDTO.class, name = "PACIENTE")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class UsuarioRequestDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellidoPaterno;

    private String apellidoMaterno;

    @NotNull
    private Genero genero;

    @NotNull
    @Past
    private LocalDate fechaNacimiento;

    @NotBlank
    @Email
    private String correo;

    @NotBlank
    private String telefono;

    @NotNull
    private Rol rol;
}
