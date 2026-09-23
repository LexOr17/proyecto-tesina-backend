package com.upemor.app_nutriologo.notificaciones.entity;

import com.upemor.app_nutriologo.notificaciones.enums.TipoNotificacion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad principal de notificación.
 * Representa el "sobre" que contiene el mensaje. Se crea una sola vez
 * y se distribuye a los destinatarios a través de {@link NotificacionUsuario}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacion tipo;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    /**
     * URL relativa para que el frontend navegue al recurso relacionado.
     * Ejemplos: "/pacientes/5", "/plantillas/3"
     */
    @Column(nullable = true, length = 500)
    private String actionUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    /**
     * Relación inversa hacia los registros por destinatario.
     */
    @Builder.Default
    @OneToMany(mappedBy = "notificacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificacionUsuario> destinatarios = new ArrayList<>();
}
