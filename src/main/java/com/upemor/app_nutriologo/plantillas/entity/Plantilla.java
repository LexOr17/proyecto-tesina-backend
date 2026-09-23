package com.upemor.app_nutriologo.plantillas.entity;

import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa una plantilla o receta predefinida de comida.
 * <p>
 * Una plantilla agrupa un conjunto de alimentos con sus porciones recomendadas
 * asociadas a un momento del día (desayuno, almuerzo, merienda o cena), creada
 * por un usuario (nutriólogo o administrador) para su reutilización en planes alimenticios.
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
@Table(name = "plantillas")
public class Plantilla {

    /**
     * Identificador único de la plantilla.
     */
    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre descriptivo o título de la plantilla.
     */
    @ToString.Include
    @Column(nullable = false, length = 100)
    private String nombre;

    /**
     * Tipo o momento de comida para el cual está diseñada la plantilla.
     */
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComida tipoComida;

    /**
     * Fecha de creación de la plantilla en el sistema.
     */
    @ToString.Include
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate fechaCreacion;

    /**
     * Usuario (nutriólogo o administrador) que creó la plantilla.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Usuario usuario;

    /**
     * Lista de alimentos y cantidades que conforman la plantilla.
     */
    @Builder.Default
    @OneToMany(mappedBy = "plantilla", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlantillaAlimento> detalles = new ArrayList<>();

    // ==========================================
    // Métodos Helper para sincronizar la relación
    // ==========================================

    /**
     * Agrega un alimento a la plantilla con una cantidad específica, sincronizando la relación bidireccional.
     *
     * @param alimento Entidad del alimento a asociar.
     * @param cantidad Cantidad del alimento asignada a la plantilla.
     */
    public void addAlimento(com.upemor.app_nutriologo.alimento.entity.Alimento alimento, BigDecimal cantidad) {
        PlantillaAlimento detalle = PlantillaAlimento.builder()
                .plantilla(this)
                .alimento(alimento)
                .cantidad(cantidad)
                .build();
        this.detalles.add(detalle);
    }

    /**
     * Remueve un detalle de alimento de la plantilla, desvinculando la relación bidireccional.
     *
     * @param detalle Registro de detalle a remover.
     */
    public void removeAlimento(PlantillaAlimento detalle) {
        this.detalles.remove(detalle);
        detalle.setPlantilla(null);
    }
}