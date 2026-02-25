package com.upemor.app_nutriologo.medidas_antropometricas.entity;

import com.upemor.app_nutriologo.paciente.entity.Paciente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "medidas_antropometricas")
public class MedidaAntropometrica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal altura;

    @Column(precision = 5, scale = 2)
    private BigDecimal cintura;

    @Column(precision = 5, scale = 2)
    private BigDecimal cadera;

    @Column(precision = 5, scale = 2)
    private BigDecimal cuello;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate fechaMedicion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;
}
