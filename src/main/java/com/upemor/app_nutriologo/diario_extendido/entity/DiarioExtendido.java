package com.upemor.app_nutriologo.diario_extendido.entity;

import com.upemor.app_nutriologo.diario_extendido.enums.CalidadSueno;
import com.upemor.app_nutriologo.diario_extendido.enums.EstadoAnimo;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Activamos la inclusión explícita
@Table(name = "diarios_extendidos")
public class DiarioExtendido {

    @Id
    @EqualsAndHashCode.Include // Lombok SOLO usará el ID para equals() y hashCode()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAnimo estadoAnimo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CalidadSueno calidadSueno;

    @Lob
    private String sintomas;

    // Inicializamos la lista para evitar NullPointerException al crear un diario nuevo
    @OneToMany(mappedBy = "diarioExtendido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BebidaConsumida> bebidas = new ArrayList<>();

    // Campos para el nutriólogo
    private String actividadFisicaSugerida;

    @Lob
    private String observacionesNutriologo;

    // --- Métodos Helper para sincronizar la relación bidireccional ---

    public void addBebida(BebidaConsumida bebida) {
        bebidas.add(bebida);
        bebida.setDiarioExtendido(this);
    }

    public void removeBebida(BebidaConsumida bebida) {
        bebidas.remove(bebida);
        bebida.setDiarioExtendido(null);
    }
}