package com.upemor.app_nutriologo.meta.entity;

import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "metas")
public class Meta {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Solo dejamos la restricción de la base de datos
    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fechaObjetivo;

    @Builder.Default
    @Column(nullable = false)
    private boolean cumplida = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutriologo_id", nullable = false)
    private Nutriologo nutriologo;
}