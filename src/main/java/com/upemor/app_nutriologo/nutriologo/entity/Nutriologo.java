package com.upemor.app_nutriologo.nutriologo.entity;

import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nutriologos")
@EqualsAndHashCode(callSuper = true)
public class Nutriologo extends Usuario {

    @Column(nullable = false, unique = true)
    private String cedulaProfesional;

}
