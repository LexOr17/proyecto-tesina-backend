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
@ToString(exclude = {"paciente", "nutriologo", "comidasProgramadas"})
@EqualsAndHashCode(of = "id")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "planes_alimenticios")
public class PlanAlimenticio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private Integer duracionDias;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate fechaCreacion;

    // A quién va dirigido el plan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario paciente;

    // Quién armó el plan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutriologo_id", nullable = false)
    private Usuario nutriologo;

    // Relación 1:N - Un plan tiene muchas comidas
    // cascade = CascadeType.ALL y orphanRemoval = true hacen que
    // al guardar el plan, se guarden o borren sus comidas automáticamente.
    @Builder.Default
    @OneToMany(mappedBy = "planAlimenticio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComidaProgramada> comidasProgramadas = new ArrayList<>();

    // ==========================================
    // Métodos Helper para sincronizar la relación bidireccional
    // ==========================================
    public void addComidaProgramada(ComidaProgramada comida) {
        comidasProgramadas.add(comida);
        comida.setPlanAlimenticio(this);
    }

    public void removeComidaProgramada(ComidaProgramada comida) {
        comidasProgramadas.remove(comida);
        comida.setPlanAlimenticio(null);
    }
}
