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

/**
 * DTO abstracto base para las solicitudes de creación y actualización de usuarios.
 * <p>
 * Define los atributos comunes que deben suministrarse en los endpoints de registro de usuarios.
 * Soporta polimorfismo dinámico en la deserialización JSON mediante la propiedad discriminadora {@code rol},
 * mapeando la solicitud a sus implementaciones concretas: {@link AdministradorRequestDTO},
 * {@link NutriologoRequestDTO} o {@link PacienteRequestDTO}.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "rol", visible = true)
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

        /**
         * Nombre(s) de pila del usuario.
         */
        @NotBlank(message = "El nombre es requerido")
        private String nombre;

        /**
         * Primer apellido (paterno) del usuario.
         */
        @NotBlank(message = "El apellido paterno es requerido")
        private String apellidoPaterno;

        /**
         * Segundo apellido (materno) del usuario, opcional.
         */
        private String apellidoMaterno;

        /**
         * Género del usuario.
         */
        @NotNull(message = "El género es requerido")
        private Genero genero;

        /**
         * Fecha de nacimiento del usuario, debe corresponder a una fecha en el pasado.
         */
        @NotNull(message = "La fecha de nacimiento es requerida")
        @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
        private LocalDate fechaNacimiento;

        /**
         * Dirección de correo electrónico única y válida del usuario.
         */
        @NotBlank(message = "El correo es requerido")
        @Email(message = "El correo debe ser válido")
        private String correo;

        /**
         * Número telefónico de contacto del usuario.
         */
        @NotBlank(message = "El teléfono es requerido")
        private String telefono;

        /**
         * Rol asignado que define el tipo de usuario y el subtipo de DTO a instanciar.
         */
        @NotNull(message = "El rol es requerido")
        private Rol rol;
}
