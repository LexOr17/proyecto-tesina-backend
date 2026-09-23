package com.upemor.app_nutriologo.config;

import com.upemor.app_nutriologo.security.AuthUser;
import com.upemor.app_nutriologo.security.CustomUserDetailsService;
import com.upemor.app_nutriologo.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/**
 * Configuración de WebSocket y mensajería STOMP para la comunicación en tiempo real.
 * <p>
 * Habilita el broker de mensajes con {@link EnableWebSocketMessageBroker}, configura los destinos
 * de suscripción para mensajería punto a punto (chat y notificaciones privadas) y difusión masiva (broadcast),
 * registra los endpoints de conexión con soporte para SockJS y define un interceptor de canal para
 * autenticar las conexiones entrantes mediante tokens JWT.
 * </p>
 * 
 * @author Sistema Nutriólogo
 * @version 1.0
 * @see WebSocketMessageBrokerConfigurer
 * @see EnableWebSocketMessageBroker
 * @see JwtUtils
 * @see CustomUserDetailsService
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Componente para la validación y extracción de información de tokens JWT.
     */
    private final JwtUtils jwtUtils;

    /**
     * Servicio para la consulta y carga de detalles del usuario autenticado.
     */
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Constructor que inyecta las dependencias necesarias para la autenticación en WebSockets.
     *
     * @param jwtUtils utilidad para el manejo de JWT.
     * @param customUserDetailsService servicio para la carga de usuarios.
     */
    public WebSocketConfig(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Configura el broker de mensajes en memoria y los prefijos de destino de la aplicación.
     * <p>
     * - {@code /queue} y {@code /user}: Utilizados para la mensajería individual uno a uno (chat y notificaciones privadas).<br>
     * - {@code /topic}: Utilizado para canales de transmisión grupal o difusión general.<br>
     * - {@code /app}: Prefijo para los mensajes enviados desde los clientes hacia los controladores del backend.<br>
     * - {@code /user}: Prefijo para el enrutamiento de destinos dirigidos a usuarios específicos (ej. con {@code SimpMessagingTemplate.convertAndSendToUser}).
     * </p>
     *
     * @param config registro de configuración del broker de mensajes.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker para llevar mensajes a los clientes.
        // "/queue" y "/user" son clave para la mensajería uno a uno (chat y notificaciones individuales).
        // "/topic" habilita canales de broadcast por rol o globales (notificaciones grupales).
        config.enableSimpleBroker("/topic", "/queue");
        // Define el prefijo para los mensajes que van del cliente al servidor.
        config.setApplicationDestinationPrefixes("/app");
        // Define el prefijo que usa `convertAndSendToUser` para construir el destino.
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Registra los endpoints STOMP a los cuales los clientes se conectarán.
     * <p>
     * Habilita el endpoint {@code /ws} permitiendo conexiones desde cualquier origen mediante
     * {@code setAllowedOriginPatterns("*")} y activa el fallback de emulación con SockJS
     * para navegadores o entornos que no soporten WebSockets nativos directamente.
     * </p>
     *
     * @param registry registro de endpoints STOMP.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registra el endpoint de conexión para los clientes.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    /**
     * Configura el canal de entrada de mensajes del cliente añadiendo interceptores de seguridad.
     * <p>
     * Registra un {@link ChannelInterceptor} que intercepta el comando {@link StompCommand#CONNECT},
     * extrae el token Bearer del encabezado nativo {@code Authorization}, valida el token JWT y
     * asocia al mensaje un {@link UsernamePasswordAuthenticationToken} personalizado cuyo
     * método {@code getName()} retorna el ID numérico del usuario en formato String. Esto permite
     * que Spring enrute correctamente los mensajes dirigidos al usuario mediante su identificador.
     * </p>
     *
     * @param registration registro de configuración para el canal de entrada de clientes.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    List<String> authorization = accessor.getNativeHeader("Authorization");
                    if (authorization == null || authorization.isEmpty()) {
                        authorization = accessor.getNativeHeader("authorization");
                    }
                    if (authorization != null && !authorization.isEmpty()) {
                        String bearerToken = authorization.get(0);
                        if (bearerToken.startsWith("Bearer ")) {
                            String token = bearerToken.substring(7);
                            try {
                                String email = jwtUtils.getUsernameFromToken(token);
                                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                                if (jwtUtils.isTokenValid(token, userDetails)) {
                                    UsernamePasswordAuthenticationToken authentication =
                                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()) {
                                                @Override
                                                public String getName() {
                                                    if (userDetails instanceof AuthUser authUser) {
                                                        return authUser.getUsuario().getId().toString();
                                                    }
                                                    return super.getName();
                                                }
                                            };
                                    accessor.setUser(authentication);
                                    if (userDetails instanceof AuthUser authUser) {
                                        log.info("[WebSocket] Usuario autenticado exitosamente: ID={}, Email={}",
                                                authUser.getUsuario().getId(), email);
                                    } else {
                                        log.info("[WebSocket] Usuario autenticado (sin AuthUser): Principal={}", authentication.getName());
                                    }
                                } else {
                                    log.warn("[WebSocket] Token JWT inválido para el usuario: {}", email);
                                }
                            } catch (Exception e) {
                                log.error("[WebSocket] Error validando token en conexión STOMP: {}", e.getMessage());
                            }
                        } else {
                            log.warn("[WebSocket] Encabezado Authorization no tiene formato Bearer");
                        }
                    } else {
                        log.warn("[WebSocket] Conexión STOMP CONNECT sin encabezado Authorization");
                    }
                }
                return message;
            }
        });
    }
}

