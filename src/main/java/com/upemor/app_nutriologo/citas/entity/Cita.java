package com.upemor.app_nutriologo.citas.entity;

import com.upemor.app_nutriologo.citas.enums.Estado;
import com.upemor.app_nutriologo.citas.enums.MotivoCita;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"paciente", "nutriologo"}) // Evita bucles infinitos en logs
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(nullable = false)
    private LocalDateTime fechaHoraFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MotivoCita motivoCita;

    @Column(length = 500)
    private String detalleAdicional;

    private String observaciones;

    @Column
    private LocalDateTime fechaHoraReprogramacionSolicitada;

    // Campos de auditoría básicos
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutriologo_id", nullable = false)
    private Nutriologo nutriologo;

    // --- Métodos de ciclo de vida JPA ---

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        // Si al guardar no tiene estado, por defecto es SOLICITADA
        if (this.estado == null) {
            this.estado = Estado.SOLICITADA;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}