package com.upemor.app_nutriologo.plan_alimenticio.entity;

import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString(exclude = {"planAlimenticio", "plantilla"})
@EqualsAndHashCode(of = "id")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comidas_programadas")
public class ComidaProgramada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A qué plan pertenece esta asignación (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_alimenticio_id", nullable = false)
    private PlanAlimenticio planAlimenticio;

    // Qué comida (plantilla) le vas a dar (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantilla_id", nullable = false)
    private Plantilla plantilla;

    // Día específico del plan (Ej: 1, 2, 3...)
    @Column(nullable = false)
    private Integer diaPlan;

    // Reutilizamos tu enum (Desayuno, Comida, Cena, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComida horario;

    // Notas específicas para este día y hora en particular
    @Column(length = 500)
    private String observaciones;
}
