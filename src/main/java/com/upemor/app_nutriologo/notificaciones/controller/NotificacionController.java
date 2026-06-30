package com.upemor.app_nutriologo.notificaciones.controller;

import com.upemor.app_nutriologo.notificaciones.dto.NotificacionDTO;
import com.upemor.app_nutriologo.notificaciones.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints REST para que el frontend gestione las notificaciones del usuario autenticado.
 *
 * <p>Todos los endpoints operan sobre el usuario autenticado — no se expone la gestión
 * de notificaciones de otros usuarios.</p>
 */
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    /**
     * Obtiene las notificaciones del usuario autenticado de manera paginada.
     * Por defecto devuelve las 20 más recientes.
     *
     * GET /api/notificaciones?page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<NotificacionDTO>> obtenerMisNotificaciones(
            @PageableDefault(size = 20, sort = "creadoEn", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<NotificacionDTO> notificaciones = notificacionService.obtenerMisNotificaciones(pageable);
        return ResponseEntity.ok(notificaciones);
    }

    /**
     * Devuelve el conteo de notificaciones no leídas del usuario autenticado.
     * Usado para mostrar el badge numérico en el frontend.
     *
     * GET /api/notificaciones/no-leidas/count
     * Respuesta: { "count": 3 }
     */
    @GetMapping("/no-leidas/count")
    public ResponseEntity<Map<String, Long>> contarNoLeidas() {
        long count = notificacionService.contarNoLeidas();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Marca una notificación específica como leída.
     * Valida que la notificación pertenezca al usuario autenticado.
     *
     * PATCH /api/notificaciones/{id}/leer
     */
    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long id) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marca TODAS las notificaciones del usuario autenticado como leídas.
     * Útil para el botón "Marcar todas como leídas" en el panel.
     *
     * PATCH /api/notificaciones/leer-todas
     */
    @PatchMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodasComoLeidas() {
        notificacionService.marcarTodasComoLeidas();
        return ResponseEntity.noContent().build();
    }
}
