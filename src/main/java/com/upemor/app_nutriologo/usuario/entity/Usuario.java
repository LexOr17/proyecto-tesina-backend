package com.upemor.app_nutriologo.usuario.entity;

import com.upemor.app_nutriologo.usuario.enums.Genero;
import com.upemor.app_nutriologo.usuario.enums.Rol;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Clase abstracta base que representa a los usuarios del sistema.
 * <p>
 * Contiene la información personal, datos de acceso y atributos comunes compartidos
 * por los diferentes roles de la plataforma (tales como pacientes, nutriólogos y administradores).
 * Utiliza una estrategia de herencia {@link InheritanceType#JOINED} para persistir la
 * especialización de entidades en las tablas correspondientes.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {

    /**
     * Identificador único autoincremental del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre o nombres de pila del usuario.
     */
    @Column(nullable = false, length = 50)
    private String nombre;

    /**
     * Primer apellido (paterno) del usuario.
     */
    @Column(nullable = false, length = 50)
    private String apellidoPaterno;

    /**
     * Segundo apellido (materno) del usuario, opcional.
     */
    @Column(nullable = true, length = 50)
    private String apellidoMaterno;

    /**
     * Género biológico o de identidad del usuario.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero genero;

    /**
     * Fecha de nacimiento del usuario.
     */
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * Dirección de correo electrónico única del usuario, empleada como identificador de inicio de sesión.
     */
    @Column(nullable = false, unique = true)
    private String correo;

    /**
     * Contraseña de acceso cifrada del usuario.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Número telefónico de contacto único del usuario.
     */
    @Column(nullable = false, unique = true)
    private String telefono;

    /**
     * Rol asignado dentro del sistema que define los permisos y acceso a las funcionalidades.
     */
    @Enumerated(EnumType.STRING)
    private Rol rol;

    /**
     * Fecha y hora en la que el usuario fue registrado en el sistema.
     */
    private LocalDateTime fechaRegistro;

    /**
     * Indicador de estado que determina si la cuenta del usuario se encuentra activa en el sistema.
     */
    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean activo;

    /**
     * Asigna la fecha y hora actual de registro y el estado activo por defecto
     * antes de persistir la entidad por primera vez en la base de datos.
     */
    @PrePersist
    public void prePersist() {
        fechaRegistro = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
    }

    /**
     * Compara la igualdad de esta entidad con otro objeto con base en su identificador primario.
     *
     * @param o objeto con el cual realizar la comparación.
     * @return {@code true} si ambos objetos representan al mismo usuario con identificador coincidente; {@code false} en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    /**
     * Genera el código hash de la entidad fundamentado en su identificador único.
     *
     * @return valor entero del código hash correspondiente al identificador del usuario.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
