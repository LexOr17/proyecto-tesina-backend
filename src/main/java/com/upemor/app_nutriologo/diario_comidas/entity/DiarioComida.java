package com.upemor.app_nutriologo.diario_comidas.entity;

import com.upemor.app_nutriologo.diario_comidas.enums.EstadoRegistro;
import com.upemor.app_nutriologo.plan_alimenticio.entity.ComidaProgramada;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true) // Ahora sí incluiremos campos
@EqualsAndHashCode(of = "id")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "diario_comida", indexes = {
        @Index(name = "idx_diario_paciente_fecha", columnList = "paciente_id, fecha")
})
public class DiarioComida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include // Útil para debugging
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comida_programada_id", nullable = true)
    private ComidaProgramada comidaProgramada;

    @ToString.Include
    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComida horario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRegistro estado;

    @Column(columnDefinition = "TEXT")
    private String detallesComidaReal;

    @Column(columnDefinition = "TEXT")
    private String observacionNutriologo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistroSistema;
}