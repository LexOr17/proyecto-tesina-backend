package com.upemor.app_nutriologo.notificaciones.entity;

import com.upemor.app_nutriologo.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Tabla pivot que relaciona una {@link Notificacion} con cada usuario destinatario.
 * Permite rastrear el estado de lectura individualmente por usuario.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notificaciones_usuarios", indexes = {
        @Index(name = "idx_nu_destinatario", columnList = "destinatario_id"),
        @Index(name = "idx_nu_leida", columnList = "destinatario_id, leida")
})
public class NotificacionUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notificacion_id", nullable = false)
    private Notificacion notificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Usuario destinatario;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean leida = false;

    @Column(nullable = true)
    private LocalDateTime fechaLeido;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    /**
     * Marca esta notificación como leída y registra el momento.
     */
    public void marcarLeida() {
        this.leida = true;
        this.fechaLeido = LocalDateTime.now();
    }
}
