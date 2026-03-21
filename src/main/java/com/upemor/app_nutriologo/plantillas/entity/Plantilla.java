package com.upemor.app_nutriologo.plantillas.entity;

import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @Column(nullable = false, length = 100)
    private String nombre;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComida tipoComida;

    @ToString.Include
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación hacia la tabla intermedia
    @Builder.Default
    @OneToMany(mappedBy = "plantilla", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlantillaAlimento> detalles = new ArrayList<>();

    // ==========================================
    // Métodos Helper para sincronizar la relación
    // ==========================================

    /**
     * Agrega un alimento a la plantilla con una cantidad específica.
     */
    public void addAlimento(com.upemor.app_nutriologo.alimento.entity.Alimento alimento, BigDecimal cantidad) {
        PlantillaAlimento detalle = PlantillaAlimento.builder()
                .plantilla(this)
                .alimento(alimento)
                .cantidad(cantidad)
                .build();
        this.detalles.add(detalle);
    }

    public void removeAlimento(PlantillaAlimento detalle) {
        this.detalles.remove(detalle);
        detalle.setPlantilla(null);
    }
}