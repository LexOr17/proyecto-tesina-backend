package com.upemor.app_nutriologo.alimento.entity;

import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true) // Seguridad estándar
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "alimentos")
public class Alimento {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @ToString.Include
    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal calorias;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal proteina;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal carbohidratos;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal grasas;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal porcionBase;

    @ToString.Include
    @Column(nullable = false, length = 20)
    private String unidadPorcion;

    @Column(length = 255)
    private String imagenUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por_id", nullable = true)
    private Usuario registradoPor;
}