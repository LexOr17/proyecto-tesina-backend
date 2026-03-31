package com.upemor.app_nutriologo.sugerencia_plantilla.entity;

import com.upemor.app_nutriologo.alimento.entity.Alimento;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import com.upemor.app_nutriologo.sugerencia_plantilla.enums.EstadoSugerencia;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "sugerencias_plantilla")
public class SugerenciaPlantilla {

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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoSugerencia estado = EstadoSugerencia.PENDIENTE;

    @Column(length = 500)
    private String motivoSugerencia;

    @Column(length = 500)
    private String comentarioNutriologo;

    /** ID de la plantilla original copiada; null si se creó desde cero */
    @Column(name = "plantilla_original_id")
    private Long plantillaOriginalId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    @Column
    private LocalDateTime fechaRevision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutriologo_id", nullable = false)
    private Nutriologo nutriologo;

    @Builder.Default
    @OneToMany(mappedBy = "sugerenciaPlantilla", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SugerenciaPlantillaDetalle> detalles = new ArrayList<>();

    // ========================================
    // Método Helper
    // ========================================

    public void addAlimento(Alimento alimento, BigDecimal cantidad) {
        SugerenciaPlantillaDetalle detalle = SugerenciaPlantillaDetalle.builder()
                .sugerenciaPlantilla(this)
                .alimento(alimento)
                .cantidad(cantidad)
                .build();
        this.detalles.add(detalle);
    }
}

