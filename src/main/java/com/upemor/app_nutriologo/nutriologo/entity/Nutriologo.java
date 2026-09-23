package com.upemor.app_nutriologo.nutriologo.entity;

import com.upemor.app_nutriologo.citas.entity.Cita;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Entidad JPA que representa a un nutriólogo dentro del sistema, extendiendo de {@link Usuario}.
 * <p>
 * Representa la tabla {@code nutriologos} mediante la estrategia de herencia compartida {@link InheritanceType#JOINED}.
 * Almacena los datos profesionales del especialista de la nutrición (como su cédula profesional)
 * y mantiene las relaciones con sus pacientes asignados y su agenda de citas.
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
@Table(name = "nutriologos")
@EqualsAndHashCode(callSuper = true)
public class Nutriologo extends Usuario {

    /**
     * Cédula profesional o número de acreditación oficial único del nutriólogo.
     */
    @Column(nullable = false, unique = true)
    private String cedulaProfesional;

    /**
     * Lista de pacientes asignados actualmente bajo el seguimiento del nutriólogo.
     */
    @OneToMany(mappedBy = "nutriologo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Paciente> pacientes;

    /**
     * Lista de consultas y citas agendadas por el nutriólogo con sus pacientes.
     */
    @OneToMany(mappedBy = "nutriologo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Cita> citas;
}
