package com.upemor.app_nutriologo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker para llevar mensajes a los clientes.
        // "/user" es clave para la mensajería uno a uno (chat y notificaciones individuales).
        // "/topic" habilita canales de broadcast por rol o globales (notificaciones grupales).
        config.enableSimpleBroker("/user", "/topic");
        // Define el prefijo para los mensajes que van del cliente al servidor.
        config.setApplicationDestinationPrefixes("/app");
        // Define el prefijo que usa `convertAndSendToUser` para construir el destino.
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registra el endpoint de conexión para los clientes.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
