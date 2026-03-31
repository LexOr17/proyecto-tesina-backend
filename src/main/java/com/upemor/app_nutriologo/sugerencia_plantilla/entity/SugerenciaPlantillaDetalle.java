package com.upemor.app_nutriologo.sugerencia_plantilla.entity;

import com.upemor.app_nutriologo.alimento.entity.Alimento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "sugerencia_plantilla_detalles")
public class SugerenciaPlantillaDetalle {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sugerencia_plantilla_id", nullable = false)
    private SugerenciaPlantilla sugerenciaPlantilla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alimento_id", nullable = false)
    private Alimento alimento;

    @ToString.Include
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal cantidad;
}

