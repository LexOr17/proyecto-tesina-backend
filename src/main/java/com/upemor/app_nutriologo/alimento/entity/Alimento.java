package com.upemor.app_nutriologo.alimento.entity;

import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alimentos")
public class Alimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

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
