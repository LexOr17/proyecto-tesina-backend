package com.upemor.app_nutriologo.plantillas.entity;

import com.upemor.app_nutriologo.alimento.entity.Alimento;
import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"usuario", "alimentos"})
@EqualsAndHashCode(of = "id")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "plantillas")
public class Plantilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComida tipoComida; 

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate fechaCreacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "plantilla_alimentos",
        joinColumns = @JoinColumn(name = "plantilla_id"),
        inverseJoinColumns = @JoinColumn(name = "alimento_id")
    )
    private List<Alimento> alimentos = new ArrayList<>();

}
