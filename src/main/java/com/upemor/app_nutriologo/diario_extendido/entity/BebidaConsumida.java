package com.upemor.app_nutriologo.diario_extendido.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "bebidas_consumidas")
public class BebidaConsumida {

    @Id
    @EqualsAndHashCode.Include // Le decimos a Lombok que SOLO use el ID para equals y hashCode
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double cantidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diario_extendido_id", nullable = false)
    private DiarioExtendido diarioExtendido;

}