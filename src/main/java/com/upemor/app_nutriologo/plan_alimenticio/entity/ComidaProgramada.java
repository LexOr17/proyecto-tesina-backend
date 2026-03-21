package com.upemor.app_nutriologo.plan_alimenticio.entity;

import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "comidas_programadas")
public class ComidaProgramada {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_alimenticio_id", nullable = false)
    private PlanAlimenticio planAlimenticio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantilla_id", nullable = false)
    private Plantilla plantilla;

    @ToString.Include
    @Column(nullable = false)
    private Integer diaPlan;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComida horario;

    @ToString.Include
    @Column(length = 500)
    private String observaciones;
}