package com.upemor.app_nutriologo.diario_comidas.entity;

import com.upemor.app_nutriologo.diario_comidas.enums.EstadoRegistro;
import com.upemor.app_nutriologo.plan_alimenticio.entity.ComidaProgramada;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un registro diario del consumo de alimentos de un paciente.
 * <p>
 * Vincula el consumo efectivo realizado por el paciente con una comida programada opcional,
 * registrando el estado de cumplimiento (cumplido, modificado, saltada), el detalle de los
 * alimentos reales consumidos y la retroalimentación u observaciones emitidas por el nutriólogo.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
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

    /**
     * Identificador único del registro de diario de comida.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include // Útil para debugging
    private Long id;

    /**
     * Paciente titular del registro de comida.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario paciente;

    /**
     * Comida programada de referencia en el plan alimenticio (opcional).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comida_programada_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private ComidaProgramada comidaProgramada;

    /**
     * Fecha correspondiente al consumo de la comida registrada.
     */
    @ToString.Include
    @Column(nullable = false)
    private LocalDate fecha;

    /**
     * Momento o tipo de comida del día (desayuno, almuerzo, merienda, cena).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComida horario;

    /**
     * Estado de cumplimiento de la comida (cumplido, modificado, saltada, no registrado).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRegistro estado;

    /**
     * Descripción detallada de los alimentos y porciones efectivamente consumidos.
     */
    @Column(columnDefinition = "TEXT")
    private String detallesComidaReal;

    /**
     * Retroalimentación o nota de seguimiento agregada por el nutriólogo.
     */
    @Column(columnDefinition = "TEXT")
    private String observacionNutriologo;

    /**
     * Marca de tiempo de registro automático generada por el sistema.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistroSistema;
}