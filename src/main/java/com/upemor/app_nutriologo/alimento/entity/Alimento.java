package com.upemor.app_nutriologo.alimento.entity;

import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad JPA que representa un alimento dentro del catálogo nutricional del sistema.
 * <p>
 * Almacena los valores nutrimentales de referencia (calorías, macronutrientes)
 * calculados sobre una porción base y unidad de medida estándar, así como metadatos de auditoría
 * y relación con el usuario que lo registró.
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
@ToString(onlyExplicitlyIncluded = true) // Seguridad estándar
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "alimentos")
public class Alimento {

    /**
     * Identificador único autoincremental del alimento.
     */
    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre descriptivo y único del alimento.
     */
    @ToString.Include
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    /**
     * Grupo o categoría nutricional a la que pertenece el alimento.
     */
    @ToString.Include
    @Column(nullable = false, length = 50)
    private String categoria;

    /**
     * Aporte calórico expresado en kilocalorías (kcal) correspondiente a la porción base.
     */
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal calorias;

    /**
     * Cantidad de proteína en gramos (g) aportada por la porción base.
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal proteina;

    /**
     * Cantidad de carbohidratos en gramos (g) aportados por la porción base.
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal carbohidratos;

    /**
     * Cantidad de grasas o lípidos en gramos (g) aportados por la porción base.
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal grasas;

    /**
     * Cantidad numérica representativa de la porción base de referencia.
     */
    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal porcionBase;

    /**
     * Unidad de medida asignada a la porción (ej. gramos, ml, pieza, taza).
     */
    @ToString.Include
    @Column(nullable = false, length = 20)
    private String unidadPorcion;

    /**
     * URL o ruta de acceso a la imagen ilustrativa del alimento.
     */
    @Column(length = 255)
    private String imagenUrl;

    /**
     * Fecha en la que el alimento fue dado de alta en la base de datos.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate fechaRegistro;

    /**
     * Usuario (nutriólogo o administrador) responsable del alta del alimento en el catálogo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Usuario registradoPor;
}