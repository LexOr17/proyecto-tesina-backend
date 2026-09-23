package com.upemor.app_nutriologo.seguimiento.entity;

import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "seguimiento_consultas", uniqueConstraints = {
        @UniqueConstraint(name = "uk_seguimiento_paciente_periodo", columnNames = { "paciente_id", "fecha_inicio",
                "fecha_fin" })
}, indexes = {
        @Index(name = "idx_seguimiento_paciente", columnList = "paciente_id, fecha_consulta")
})
public class SeguimientoConsulta {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    // ── Relaciones ───────────────────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutriologo_id", nullable = false)
    private Nutriologo nutriologo;

    // ── Período revisado ─────────────────────────────────────────────────────
    @ToString.Include
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @ToString.Include
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    // ── Snapshot KPIs (inmutables tras creación) ─────────────────────────────
    @Column(precision = 5, scale = 2)
    private BigDecimal pesoInicio;

    @Column(precision = 5, scale = 2)
    private BigDecimal pesoFin;

    @Column(precision = 5, scale = 2)
    private BigDecimal diferenciaPeso;

    private Integer totalComidasRegistradas;
    private Integer comidasCumplidas;
    private Integer comidasModificadas;
    private Integer comidasSaltadas;

    @Column(precision = 5, scale = 2)
    private BigDecimal pctCumplimientoAlimentos;

    private Integer diasActividadRealizada;
    private Integer totalDiasExtendidos;

    @Column(precision = 5, scale = 2)
    private BigDecimal pctActividad;

    private Long totalMetas;
    private Long metasCumplidas;
    private Long totalCitas;
    private Long citasCompletadas;

    // ── Texto del nutriólogo (editables) ──────────────────────────────────────
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(columnDefinition = "TEXT")
    private String conclusiones;

    @Column(columnDefinition = "TEXT")
    private String recomendaciones;

    // ── Auditoría ────────────────────────────────────────────────────────────
    @CreationTimestamp
    @Column(name = "fecha_consulta", nullable = false, updatable = false)
    @ToString.Include
    private LocalDateTime fechaConsulta;

    @UpdateTimestamp
    private LocalDateTime actualizadoEn;
}
