package com.upemor.app_nutriologo.diario_extendido.entity;

import com.upemor.app_nutriologo.diario_extendido.enums.CalidadSueno;
import com.upemor.app_nutriologo.diario_extendido.enums.EstadoAnimo;
import com.upemor.app_nutriologo.diario_extendido.enums.Sintoma;
import com.upemor.app_nutriologo.diario_extendido.enums.TipoActividad;
import com.upemor.app_nutriologo.paciente.entity.Paciente;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "diarios_extendidos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"paciente_id", "fecha"})
})
public class DiarioExtendido {

    @Id
    @EqualsAndHashCode.Include // Lombok SOLO usará el ID para equals() y hashCode()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private EstadoAnimo estadoAnimo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private CalidadSueno calidadSueno;

    @Enumerated(EnumType.STRING)
    private Sintoma sintomaPrincipal = Sintoma.NINGUNO;
    @Column(columnDefinition = "TEXT")
    private String sintomaDetalle;

    // Inicializamos la lista para evitar NullPointerException al crear un diario nuevo
    @Builder.Default
    @OneToMany(mappedBy = "diarioExtendido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BebidaConsumida> bebidas = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TipoActividad tipoActividadSugerida = TipoActividad.NINGUNA;
    private String actividadDetalle; // Instrucciones específicas
    @Column(columnDefinition = "TEXT")
    private String observacionesNutriologo;

    @Column(nullable = true)
    private Boolean actividadRealizada;


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