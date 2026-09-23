package com.upemor.app_nutriologo.historial_medico.entity;

import com.upemor.app_nutriologo.historial_medico.enums.EnfermedadEnum;
import com.upemor.app_nutriologo.historial_medico.enums.EstadoMedicamento;
import com.upemor.app_nutriologo.historial_medico.enums.SintomaEnum;
import com.upemor.app_nutriologo.medidas_antropometricas.entity.MedidaAntropometrica;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
@Table(name = "historial_medico")
public class HistorialMedico {

    @Id
    @ToString.Include
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── Enfermedades ────────────────────────────────────────────────────────

    /** Lista de enfermedades conocidas del paciente (selección múltiple) */
    @ElementCollection(targetClass = EnfermedadEnum.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "historial_enfermedad", joinColumns = @JoinColumn(name = "historial_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "enfermedad")
    @Builder.Default
    private List<EnfermedadEnum> enfermedades = new ArrayList<>();

    /** Descripción libre cuando se selecciona EnfermedadEnum.OTRA */
    @Column(columnDefinition = "TEXT")
    private String otraEnfermedad;

    // ─── Alergias ─────────────────────────────────────────────────────────────

    /** Alergias conocidas del paciente (texto libre) */
    @Column(columnDefinition = "TEXT")
    private String alergias;

    // ─── Síntomas ─────────────────────────────────────────────────────────────

    /** Lista de síntomas actuales del paciente (selección múltiple) */
    @ElementCollection(targetClass = SintomaEnum.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "historial_sintoma", joinColumns = @JoinColumn(name = "historial_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "sintoma")
    @Builder.Default
    private List<SintomaEnum> sintomas = new ArrayList<>();

    /** Descripción libre cuando se selecciona SintomaEnum.OTRO */
    @Column(columnDefinition = "TEXT")
    private String otroSintoma;

    // ─── Medicamentos / Suplementos ───────────────────────────────────────────

    /**
     * Estado actual respecto al consumo de medicamentos o suplementos.
     * NUNCA / ACTUALMENTE / ANTERIORMENTE
     */
    @ToString.Include
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMedicamento estadoMedicamento;

    /**
     * Nombre, dosis y frecuencia del medicamento o suplemento.
     * Obligatorio cuando estadoMedicamento es ACTUALMENTE o ANTERIORMENTE.
     */
    @Column(columnDefinition = "TEXT")
    private String descripcionMedicamento;

    // ─── Auditoría ────────────────────────────────────────────────────────────

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    // ─── Relaciones ───────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    /**
     * Medidas antropométricas asociadas a este registro del historial.
     * Permite vincular la medición tomada en la misma consulta.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medida_antropometrica_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private MedidaAntropometrica medidaAntropometrica;
}
