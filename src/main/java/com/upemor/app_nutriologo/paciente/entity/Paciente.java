package com.upemor.app_nutriologo.paciente.entity;

import com.upemor.app_nutriologo.citas.entity.Cita;
import com.upemor.app_nutriologo.diario_comidas.entity.DiarioComida;
import com.upemor.app_nutriologo.diario_extendido.entity.DiarioExtendido;
import com.upemor.app_nutriologo.historial_medico.entity.HistorialMedico;
import com.upemor.app_nutriologo.medidas_antropometricas.entity.MedidaAntropometrica;
import com.upemor.app_nutriologo.meta.entity.Meta;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.plan_alimenticio.entity.PlanAlimenticio;
import com.upemor.app_nutriologo.sugerencia_plantilla.entity.SugerenciaPlantilla;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Entidad JPA que representa a un paciente dentro del sistema, extendiendo de {@link Usuario}.
 * <p>
 * Representa la tabla {@code pacientes} mediante la estrategia de herencia compartida.
 * Centraliza la relación obligatoria con el nutriólogo asignado y agrupa las relaciones
 * con sus expedientes médicos, medidas antropométricas, citas, metas, planes y diarios alimenticios.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pacientes")
@EqualsAndHashCode(callSuper = true)
public class Paciente extends Usuario {

    /**
     * Nutriólogo profesional asignado a la atención y supervisión del paciente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutriologo_id", nullable = false)
    private Nutriologo nutriologo;

    /**
     * Historial de mediciones corporales y antropométricas registradas para el paciente.
     */
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedidaAntropometrica> medidasAntropometricas;

    /**
     * Colección de citas médicas y consultas agendadas para el paciente.
     */
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Cita> citas;

    /**
     * Metas de salud, peso y nutrición establecidas para el paciente.
     */
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meta> metas;

    /**
     * Antecedentes clínicos y expedientes médicos del paciente.
     */
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HistorialMedico> historialesMedicos;

    /**
     * Registros diarios de consumo de alimentos y comidas realizados por el paciente.
     */
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DiarioComida> diariosComida;

    /**
     * Registros de monitoreo de hábitos complementarios (consumo de agua, calidad de sueño, síntomas y ánimo).
     */
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DiarioExtendido> diariosExtendidos;

    /**
     * Planes nutricionales y alimenticios prescritos al paciente por su nutriólogo.
     */
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PlanAlimenticio> planesAlimenticios;

    /**
     * Sugerencias de adaptación o solicitudes sobre plantillas de alimentación enviadas para el paciente.
     */
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SugerenciaPlantilla> sugerenciasPlantilla;
}
