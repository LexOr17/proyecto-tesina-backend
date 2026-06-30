package com.upemor.app_nutriologo.notificaciones.service;

import com.upemor.app_nutriologo.notificaciones.dto.NotificacionDTO;
import com.upemor.app_nutriologo.notificaciones.enums.TipoNotificacion;
import com.upemor.app_nutriologo.usuario.enums.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Servicio central para el envío y gestión de notificaciones en tiempo real.
 *
 * <p>Todos los métodos de envío excluyen automáticamente al usuario autenticado
 * como destinatario (el que dispara la acción no se notifica a sí mismo).</p>
 *
 * <p>Cada notificación se persiste en base de datos para garantizar entrega
 * a usuarios que estén offline, y se envía por WebSocket a los que estén conectados.</p>
 */
public interface NotificacionService {

    /**
     * Envía una notificación a un usuario específico.
     * Si el usuario es el mismo que el autenticado, no se envía.
     *
     * @param usuarioId ID del destinatario
     * @param tipo      tipo de notificación
     * @param titulo    título corto
     * @param mensaje   cuerpo del mensaje
     * @param actionUrl URL relativa de navegación (puede ser null)
     */
    void enviarAUsuario(Long usuarioId,
                        TipoNotificacion tipo,
                        String titulo,
                        String mensaje,
                        String actionUrl);

    /**
     * Envía una notificación a todos los usuarios activos con un rol específico,
     * excluyendo al usuario autenticado.
     *
     * @param rol       rol destinatario
     * @param tipo      tipo de notificación
     * @param titulo    título corto
     * @param mensaje   cuerpo del mensaje
     * @param actionUrl URL relativa de navegación (puede ser null)
     */
    void enviarARol(Rol rol,
                    TipoNotificacion tipo,
                    String titulo,
                    String mensaje,
                    String actionUrl);

    /**
     * Envía una notificación a todos los usuarios activos con alguno de los roles indicados,
     * excluyendo al usuario autenticado.
     *
     * @param roles     lista de roles destinatarios
     * @param tipo      tipo de notificación
     * @param titulo    título corto
     * @param mensaje   cuerpo del mensaje
     * @param actionUrl URL relativa de navegación (puede ser null)
     */
    void enviarARoles(List<Rol> roles,
                      TipoNotificacion tipo,
                      String titulo,
                      String mensaje,
                      String actionUrl);

    /**
     * Envía una notificación a TODOS los usuarios activos del sistema,
     * excluyendo al usuario autenticado.
     *
     * @param tipo      tipo de notificación
     * @param titulo    título corto
     * @param mensaje   cuerpo del mensaje
     * @param actionUrl URL relativa de navegación (puede ser null)
     */
    void enviarATodos(TipoNotificacion tipo,
                      String titulo,
                      String mensaje,
                      String actionUrl);

    // =========================================================
    // Métodos de consulta (para el usuario autenticado)
    // =========================================================

    /**
     * Obtiene las notificaciones del usuario autenticado de manera paginada.
     *
     * @param pageable parámetros de paginación
     * @return página de notificaciones ordenadas por fecha descendente
     */
    Page<NotificacionDTO> obtenerMisNotificaciones(Pageable pageable);

    /**
     * Cuenta las notificaciones no leídas del usuario autenticado.
     * Útil para mostrar el badge en el frontend.
     *
     * @return número de notificaciones sin leer
     */
    long contarNoLeidas();

    /**
     * Marca una notificación específica como leída.
     *
     * @param notificacionUsuarioId ID del registro {@code NotificacionUsuario}
     */
    void marcarComoLeida(Long notificacionUsuarioId);

    /**
     * Marca todas las notificaciones del usuario autenticado como leídas.
     */
    void marcarTodasComoLeidas();
}
