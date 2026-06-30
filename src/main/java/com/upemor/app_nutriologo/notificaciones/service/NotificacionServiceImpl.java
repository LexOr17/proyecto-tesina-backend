package com.upemor.app_nutriologo.notificaciones.service;

import com.upemor.app_nutriologo.common.exceptions.ResourceNotFoundException;
import com.upemor.app_nutriologo.notificaciones.dto.NotificacionDTO;
import com.upemor.app_nutriologo.notificaciones.entity.Notificacion;
import com.upemor.app_nutriologo.notificaciones.entity.NotificacionUsuario;
import com.upemor.app_nutriologo.notificaciones.enums.TipoNotificacion;
import com.upemor.app_nutriologo.notificaciones.repository.NotificacionRepository;
import com.upemor.app_nutriologo.notificaciones.repository.NotificacionUsuarioRepository;
import com.upemor.app_nutriologo.security.UserContextService;
import com.upemor.app_nutriologo.usuario.entity.Usuario;
import com.upemor.app_nutriologo.usuario.enums.Rol;
import com.upemor.app_nutriologo.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private static final String DESTINO_WEBSOCKET = "/queue/notificaciones";

    private final NotificacionRepository notificacionRepository;
    private final NotificacionUsuarioRepository notificacionUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final UserContextService userContextService;
    private final SimpMessagingTemplate messagingTemplate;

    // =========================================================
    // Métodos de envío
    // =========================================================

    @Override
    @Transactional
    public void enviarAUsuario(Long usuarioId,
                               TipoNotificacion tipo,
                               String titulo,
                               String mensaje,
                               String actionUrl) {

        Long emisorId = userContextService.getCurrentUserId();

        // El emisor no se notifica a sí mismo
        if (emisorId.equals(usuarioId)) {
            log.debug("NotificacionService: emisor {} es el mismo destinatario, se omite.", emisorId);
            return;
        }

        Usuario destinatario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario destinatario no encontrado: " + usuarioId));

        Notificacion notificacion = crearYPersistirNotificacion(tipo, titulo, mensaje, actionUrl);
        NotificacionUsuario nu = crearRegistroDestinatario(notificacion, destinatario);

        enviarPorWebSocket(nu);
    }

    @Override
    @Transactional
    public void enviarARol(Rol rol,
                           TipoNotificacion tipo,
                           String titulo,
                           String mensaje,
                           String actionUrl) {
        enviarARoles(List.of(rol), tipo, titulo, mensaje, actionUrl);
    }

    @Override
    @Transactional
    public void enviarARoles(List<Rol> roles,
                             TipoNotificacion tipo,
                             String titulo,
                             String mensaje,
                             String actionUrl) {

        Long emisorId = userContextService.getCurrentUserId();

        List<Usuario> destinatarios = usuarioRepository.findAll().stream()
                .filter(u -> u.getActivo() != null && u.getActivo())
                .filter(u -> roles.contains(u.getRol()))
                .filter(u -> !u.getId().equals(emisorId))  // excluir al emisor
                .collect(Collectors.toList());

        if (destinatarios.isEmpty()) {
            log.debug("NotificacionService: sin destinatarios para roles {}", roles);
            return;
        }

        Notificacion notificacion = crearYPersistirNotificacion(tipo, titulo, mensaje, actionUrl);

        for (Usuario destinatario : destinatarios) {
            NotificacionUsuario nu = crearRegistroDestinatario(notificacion, destinatario);
            enviarPorWebSocket(nu);
        }
    }

    @Override
    @Transactional
    public void enviarATodos(TipoNotificacion tipo,
                             String titulo,
                             String mensaje,
                             String actionUrl) {

        Long emisorId = userContextService.getCurrentUserId();

        List<Usuario> destinatarios = usuarioRepository.findAll().stream()
                .filter(u -> u.getActivo() != null && u.getActivo())
                .filter(u -> !u.getId().equals(emisorId))  // excluir al emisor
                .collect(Collectors.toList());

        if (destinatarios.isEmpty()) {
            log.debug("NotificacionService: sin destinatarios para broadcast global.");
            return;
        }

        Notificacion notificacion = crearYPersistirNotificacion(tipo, titulo, mensaje, actionUrl);

        for (Usuario destinatario : destinatarios) {
            NotificacionUsuario nu = crearRegistroDestinatario(notificacion, destinatario);
            enviarPorWebSocket(nu);
        }
    }

    // =========================================================
    // Métodos de consulta
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<NotificacionDTO> obtenerMisNotificaciones(Pageable pageable) {
        Long usuarioId = userContextService.getCurrentUserId();
        return notificacionUsuarioRepository
                .findByDestinatarioIdOrderByCreadoEnDesc(usuarioId, pageable)
                .map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarNoLeidas() {
        Long usuarioId = userContextService.getCurrentUserId();
        return notificacionUsuarioRepository.countByDestinatarioIdAndLeidaFalse(usuarioId);
    }

    @Override
    @Transactional
    public void marcarComoLeida(Long notificacionUsuarioId) {
        Long usuarioId = userContextService.getCurrentUserId();

        NotificacionUsuario nu = notificacionUsuarioRepository.findById(notificacionUsuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notificación no encontrada con ID: " + notificacionUsuarioId));

        // Verificar que la notificación pertenece al usuario autenticado
        if (!nu.getDestinatario().getId().equals(usuarioId)) {
            throw new ResourceNotFoundException(
                    "Notificación no encontrada con ID: " + notificacionUsuarioId);
        }

        nu.marcarLeida();
        notificacionUsuarioRepository.save(nu);
    }

    @Override
    @Transactional
    public void marcarTodasComoLeidas() {
        Long usuarioId = userContextService.getCurrentUserId();
        int actualizadas = notificacionUsuarioRepository
                .marcarTodasLeidasPorUsuario(usuarioId, LocalDateTime.now());
        log.debug("NotificacionService: {} notificaciones marcadas como leídas para usuario {}",
                actualizadas, usuarioId);
    }

    // =========================================================
    // Métodos privados de apoyo
    // =========================================================

    /**
     * Crea y persiste la entidad {@link Notificacion} principal.
     */
    private Notificacion crearYPersistirNotificacion(TipoNotificacion tipo,
                                                      String titulo,
                                                      String mensaje,
                                                      String actionUrl) {
        Notificacion notificacion = Notificacion.builder()
                .tipo(tipo)
                .titulo(titulo)
                .mensaje(mensaje)
                .actionUrl(actionUrl)
                .build();
        return notificacionRepository.save(notificacion);
    }

    /**
     * Crea y persiste el registro {@link NotificacionUsuario} para un destinatario.
     */
    private NotificacionUsuario crearRegistroDestinatario(Notificacion notificacion, Usuario destinatario) {
        NotificacionUsuario nu = NotificacionUsuario.builder()
                .notificacion(notificacion)
                .destinatario(destinatario)
                .leida(false)
                .build();
        return notificacionUsuarioRepository.save(nu);
    }

    /**
     * Envía el DTO de notificación al usuario específico a través de WebSocket (STOMP).
     * Si el usuario no está conectado, el mensaje no llega en este momento,
     * pero quedó persistido en BD para su consulta posterior.
     */
    private void enviarPorWebSocket(NotificacionUsuario nu) {
        try {
            NotificacionDTO dto = toDTO(nu);
            messagingTemplate.convertAndSendToUser(
                    nu.getDestinatario().getId().toString(),
                    DESTINO_WEBSOCKET,
                    dto
            );
        } catch (Exception ex) {
            // No interrumpir el flujo si el envío WebSocket falla;
            // la notificación ya está persistida en BD.
            log.warn("NotificacionService: no se pudo enviar WebSocket a usuario {}: {}",
                    nu.getDestinatario().getId(), ex.getMessage());
        }
    }

    /**
     * Mapea un {@link NotificacionUsuario} a su representación DTO.
     */
    private NotificacionDTO toDTO(NotificacionUsuario nu) {
        Notificacion n = nu.getNotificacion();
        return NotificacionDTO.builder()
                .notificacionUsuarioId(nu.getId())
                .notificacionId(n.getId())
                .tipo(n.getTipo())
                .titulo(n.getTitulo())
                .mensaje(n.getMensaje())
                .actionUrl(n.getActionUrl())
                .leida(nu.getLeida())
                .creadoEn(nu.getCreadoEn())
                .build();
    }
}
