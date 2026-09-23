package com.upemor.app_nutriologo.plan_alimenticio.entity;

import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa un plan alimenticio o régimen nutricional asignado a un paciente.
 * <p>
 * Define el periodo de vigencia, la duración en días y agrupa el conjunto de comidas programadas
 * ({@link ComidaProgramada}) estructuradas por día y horario, vinculando tanto al paciente beneficiario
 * como al nutriólogo prescriptor.
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
@ToString(onlyExplicitlyIncluded = true) // Apagamos todo por defecto por seguridad
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "planes_alimenticios")
public class PlanAlimenticio {

    /**
     * Identificador único autoincremental del plan alimenticio.
     */
    @Id
    @ToString.Include // Solo incluimos campos básicos (nunca relaciones) en el toString
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre descriptivo asignado al plan alimenticio.
     */
    @ToString.Include
    @Column(nullable = false, length = 150)
    private String nombre;

    /**
     * Fecha de inicio en la que el paciente debe comenzar a seguir el plan.
     */
    @ToString.Include
    @Column(nullable = false)
    private LocalDate fechaInicio;

    /**
     * Duración en días contemplada para este plan alimenticio.
     */
    @ToString.Include
    @Column(nullable = false)
    private Integer duracionDias;

    /**
     * Fecha de creación y alta del plan en el sistema.
     */
    @ToString.Include
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate fechaCreacion;

    /**
     * Paciente destinatario al que se le prescribe el plan alimenticio.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario paciente;

    /**
     * Nutriólogo profesional que elaboró y prescribió el plan.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutriologo_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Usuario nutriologo;

    /**
     * Listado de comidas y tomas programadas dentro del marco del plan.
     */
    @Builder.Default
    @OneToMany(mappedBy = "planAlimenticio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComidaProgramada> comidasProgramadas = new ArrayList<>();

    // --- Métodos Helper ---

    /**
     * Agrega una comida programada al plan y sincroniza la referencia bidireccional.
     *
     * @param comida comida programada a vincular.
     */
    public void addComidaProgramada(ComidaProgramada comida) {
        comidasProgramadas.add(comida);
        comida.setPlanAlimenticio(this);
    }

    /**
     * Desvincula una comida programada del plan alimenticio.
     *
     * @param comida comida programada a remover.
     */
    public void removeComidaProgramada(ComidaProgramada comida) {
        comidasProgramadas.remove(comida);
        comida.setPlanAlimenticio(null);
    }
}