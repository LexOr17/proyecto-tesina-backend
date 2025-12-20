package com.upemor.app_nutriologo.nutriologo.entity;

import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

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

    @OneToMany(mappedBy = "nutriologo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paciente> pacientes;
}
