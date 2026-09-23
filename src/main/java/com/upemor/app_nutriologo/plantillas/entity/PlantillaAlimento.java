package com.upemor.app_nutriologo.plantillas.entity;

import com.upemor.app_nutriologo.alimento.entity.Alimento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entidad JPA que representa la relación intermedia entre una plantilla y un alimento.
 * <p>
 * Define la cantidad específica de un alimento determinado asignada dentro de una
 * plantilla de comida nutricional.
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
@Table(name = "plantilla_alimentos")
public class PlantillaAlimento {

    /**
     * Identificador único del registro de detalle de la plantilla.
     */
    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Plantilla a la que pertenece este detalle de alimento.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantilla_id", nullable = false)
    private Plantilla plantilla;

    /**
     * Alimento asignado dentro de la plantilla.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alimento_id", nullable = false)
    private Alimento alimento;

    /**
     * Cantidad asignada del alimento en la plantilla.
     */
    @ToString.Include
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal cantidad;
}