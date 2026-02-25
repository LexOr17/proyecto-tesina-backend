package com.upemor.app_nutriologo.paciente.entity;

import com.upemor.app_nutriologo.medidas_antropometricas.entity.MedidaAntropometrica;
import com.upemor.app_nutriologo.nutriologo.entity.Nutriologo;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pacientes")
@EqualsAndHashCode(callSuper = true)
public class Paciente extends Usuario {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutriologo_id", nullable = false)
    private Nutriologo nutriologo;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedidaAntropometrica> medidasAntropometricas;
}
