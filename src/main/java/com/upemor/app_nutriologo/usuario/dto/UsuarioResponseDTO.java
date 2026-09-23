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

/**
 * DTO abstracto base para las respuestas que devuelven información de usuarios.
 * <p>
 * Encapsula los datos generales de un usuario expuestos hacia los clientes de la API,
 * omitiendo información sensible como credenciales de acceso.
 * Soporta serialización polimórfica mediante la propiedad {@code rol}, discriminando entre
 * {@link AdministradorResponseDTO}, {@link NutriologoResponseDTO} y {@link PacienteResponseDTO}.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
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

    /**
     * Identificador único del usuario.
     */
    private Long id;

    /**
     * Nombre(s) de pila del usuario.
     */
    private String nombre;

    /**
     * Primer apellido (paterno) del usuario.
     */
    private String apellidoPaterno;

    /**
     * Segundo apellido (materno) del usuario.
     */
    private String apellidoMaterno;

    /**
     * Género registrado del usuario.
     */
    private Genero genero;

    /**
     * Fecha de nacimiento del usuario.
     */
    private LocalDate fechaNacimiento;

    /**
     * Dirección de correo electrónico del usuario.
     */
    private String correo;

    /**
     * Número telefónico de contacto del usuario.
     */
    private String telefono;

    /**
     * Rol asignado al usuario en el sistema.
     */
    private Rol rol;

    /**
     * Estado que indica si la cuenta del usuario se encuentra habilitada en el sistema.
     */
    private Boolean activo;

}
