package com.upemor.app_nutriologo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS (Cross-Origin Resource Sharing) para la
 * aplicación.
 * <p>
 * Esta clase define las políticas de intercambio de recursos de origen cruzado,
 * permitiendo que las aplicaciones cliente (por ejemplo, el frontend
 * desarrollado en Angular
 * que se ejecuta en {@code http://localhost:4200}) puedan comunicarse con los
 * endpoints
 * de la API REST del backend sin restricciones de origen.
 * </p>
 * 
 */
@Configuration
public class CorsConfig {

    /**
     * Configura y registra las reglas de mapeo CORS para la aplicación web.
     * <p>
     * Habilita el acceso a todos los endpoints ({@code /**}) desde el origen
     * permitido ({@code http://localhost:4200}) soportando los métodos HTTP
     * estándar: GET, POST, PUT, DELETE y PATCH.
     * </p>
     *
     * @return una instancia de {@link WebMvcConfigurer} con las reglas CORS
     *         personalizadas.
     */
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                        .allowedOrigins("http://localhost:4200");
            }
        };
    }
}