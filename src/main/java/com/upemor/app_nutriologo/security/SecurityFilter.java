package com.upemor.app_nutriologo.security;

import com.upemor.app_nutriologo.common.exceptions.CustomAccessDeniedHandler;
import com.upemor.app_nutriologo.common.exceptions.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración central de seguridad de la aplicación con Spring Security.
 * <p>
 * Define la cadena de filtros de seguridad ({@link SecurityFilterChain}), habilita la seguridad
 * basada en métodos con {@code @EnableMethodSecurity(prePostEnabled = true)}, configura la política de
 * sesiones sin estado (Stateless), gestiona las rutas públicas y protegidas, y provee los beans de
 * {@link PasswordEncoder} y {@link AuthenticationManager}.
 * </p>
 * 
 * @author Sistema Nutriólogo
 * @version 1.0
 * @see SecurityFilterChain
 * @see AuthFilter
 * @see CustomAuthenticationEntryPoint
 * @see CustomAccessDeniedHandler
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityFilter {

    /**
     * Filtro personalizado para la validación y extracción de tokens JWT.
     */
    private final AuthFilter authFilter;

    /**
     * Manejador de excepciones cuando un usuario no autenticado intenta acceder a un recurso protegido (401).
     */
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    /**
     * Manejador de excepciones cuando un usuario autenticado carece de los privilegios requeridos (403).
     */
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * Configura la cadena de filtros de seguridad HTTP (SecurityFilterChain).
     * <p>
     * - Deshabilita CSRF al tratarse de una API REST sin estado (Stateless).<br>
     * - Habilita CORS con la configuración por defecto o definida.<br>
     * - Registra los manejadores personalizados de excepciones ({@code authenticationEntryPoint} y {@code accessDeniedHandler}).<br>
     * - Define rutas públicas permitidas ({@code /login/**}, {@code /uploads/**}, {@code /ws/**}) y requiere autenticación para el resto.<br>
     * - Establece la creación de sesiones como {@link SessionCreationPolicy#STATELESS}.<br>
     * - Registra {@link AuthFilter} antes del {@link UsernamePasswordAuthenticationFilter}.
     * </p>
     *
     * @param httpSecurity configurador de seguridad HTTP de Spring Security.
     * @return la instancia construida de {@link SecurityFilterChain}.
     * @throws Exception si ocurre algún error al construir la configuración de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/login/**", "/uploads/**", "/ws/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    /**
     * Define el bean del codificador de contraseñas de la aplicación.
     * <p>
     * Utiliza el algoritmo de hashing {@link BCryptPasswordEncoder} con salt para almacenar
     * y validar contraseñas de manera segura.
     * </p>
     *
     * @return una instancia de {@link PasswordEncoder} basada en BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expone el {@link AuthenticationManager} de Spring Security a partir de la configuración global.
     *
     * @param authenticationConfiguration configuración de autenticación de Spring Security.
     * @return instancia de {@link AuthenticationManager} lista para procesar autenticaciones.
     * @throws Exception si ocurre un error al obtener el gestor de autenticación.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}

