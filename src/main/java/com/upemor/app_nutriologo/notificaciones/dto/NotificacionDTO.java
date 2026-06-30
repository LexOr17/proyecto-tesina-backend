package com.upemor.app_nutriologo.notificaciones.dto;

import com.upemor.app_nutriologo.notificaciones.enums.TipoNotificacion;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO que se envía tanto por WebSocket (tiempo real) como en las respuestas REST.
 * Contiene toda la información necesaria para que el frontend muestre y gestione
 * la notificación, incluyendo la URL de navegación.
 */
@Data
@Builder
public class NotificacionDTO {

    /**
     * ID del registro {@code NotificacionUsuario} — se usa para marcar como leída.
     */
    private Long notificacionUsuarioId;

    /**
     * ID de la notificación base (útil para agrupar o deduplicar en el frontend).
     */
    private Long notificacionId;

    private TipoNotificacion tipo;

    private String titulo;

    private String mensaje;

    /**
     * URL relativa para navegar al recurso. Ejemplo: "/pacientes/5".
     * Puede ser nula si la notificación no tiene recurso asociado.
     */
    private String actionUrl;

    private Boolean leida;

    private LocalDateTime creadoEn;
}
