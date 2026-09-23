package com.upemor.app_nutriologo.plan_alimenticio.entity;

import com.upemor.app_nutriologo.plantillas.entity.Plantilla;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad JPA que representa una comida específica calendarizada dentro de un plan alimenticio.
 * <p>
 * Vincula un día secuencial del plan ({@code diaPlan}) y un tiempo de comida ({@link TipoComida})
 * con una plantilla prediseñada ({@link Plantilla}) que contiene los alimentos y porciones recomendadas,
 * además de permitir observaciones personalizadas.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
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

    /**
     * Identificador único autoincremental de la comida programada.
     */
    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Plan alimenticio al cual se encuentra adscrita esta comida programada.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_alimenticio_id", nullable = false)
    private PlanAlimenticio planAlimenticio;

    /**
     * Plantilla de platillo asignada para este tiempo de comida.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantilla_id", nullable = false)
    private Plantilla plantilla;

    /**
     * Número de día secuencial dentro de la duración del plan alimenticio (ej. 1, 2, 7).
     */
    @ToString.Include
    @Column(nullable = false)
    private Integer diaPlan;

    /**
     * Tiempo u horario de la comida (ej. DESAYUNO, COMIDA, CENA, COLACION_1, COLACION_2).
     */
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComida horario;

    /**
     * Indicaciones o notas nutricionales particulares para este tiempo de comida.
     */
    @ToString.Include
    @Column(length = 500)
    private String observaciones;
}