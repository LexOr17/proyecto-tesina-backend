package com.upemor.app_nutriologo.plan_alimenticio.entity;

import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true) // Apagamos todo por defecto por seguridad
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "planes_alimenticios")
public class PlanAlimenticio {

    @Id
    @ToString.Include // Solo incluimos campos básicos (nunca relaciones) en el toString
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @Column(nullable = false, length = 150)
    private String nombre;

    @ToString.Include
    @Column(nullable = false)
    private LocalDate fechaInicio;

    @ToString.Include
    @Column(nullable = false)
    private Integer duracionDias;

    @ToString.Include
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutriologo_id", nullable = false)
    private Usuario nutriologo;

    @Builder.Default
    @OneToMany(mappedBy = "planAlimenticio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComidaProgramada> comidasProgramadas = new ArrayList<>();

    // --- Métodos Helper ---

    public void addComidaProgramada(ComidaProgramada comida) {
        comidasProgramadas.add(comida);
        comida.setPlanAlimenticio(this);
    }

    public void removeComidaProgramada(ComidaProgramada comida) {
        comidasProgramadas.remove(comida);
        comida.setPlanAlimenticio(null);
    }
}