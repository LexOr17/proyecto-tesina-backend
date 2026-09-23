package com.upemor.app_nutriologo.administrador.entity;

import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Entidad JPA que representa a un usuario administrador dentro del sistema, extendiendo de {@link Usuario}.
 * <p>
 * Representa la tabla {@code administradores} mediante la estrategia de herencia compartida.
 * Otorga privilegios elevados de gestión sobre usuarios, configuración global y auditoría de la plataforma.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "administradores")
@EqualsAndHashCode(callSuper = true)
public class Administrador extends Usuario {

    /**
     * Nivel o clasificación de administración asignada (ej. SUPER_ADMIN, SOPORTE).
     */
    private String tipoAdmin;

}
