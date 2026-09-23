package com.upemor.app_nutriologo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación personalizado que intercepta cada solicitud HTTP entrante.
 * <p>
 * Hereda de {@link OncePerRequestFilter} para garantizar una única ejecución por cada petición.
 * Se encarga de extraer el token JWT de la cabecera {@code Authorization}, validar su firma y vigencia,
 * cargar los detalles del usuario a través de {@link CustomUserDetailsService} y establecer
 * la autenticación correspondiente en el {@link SecurityContextHolder}.
 * </p>
 * 
 * @author Sistema Nutriólogo
 * @version 1.0
 * @see OncePerRequestFilter
 * @see JwtUtils
 * @see CustomUserDetailsService
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    /**
     * Utilidad para la validación y extracción de información del token JWT.
     */
    private final JwtUtils jwtUtils;

    /**
     * Servicio para la carga de detalles del usuario desde la base de datos.
     */
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Intercepta la petición HTTP para procesar la autenticación basada en JWT.
     * <p>
     * Si la petición incluye un token Bearer válido, se recupera el usuario correspondiente,
     * se verifica el token y se almacena un {@link UsernamePasswordAuthenticationToken}
     * en el contexto de seguridad. En caso de fallo o excepción durante la validación,
     * se retorna una respuesta HTTP 401 Unauthorized con formato JSON.
     * </p>
     *
     * @param request objeto {@link HttpServletRequest} que contiene la solicitud del cliente.
     * @param response objeto {@link HttpServletResponse} para enviar la respuesta HTTP.
     * @param filterChain cadena de filtros de Spring Security para continuar con el procesamiento.
     * @throws ServletException si ocurre un error durante el procesamiento del servlet.
     * @throws IOException si ocurre un error de entrada/salida al procesar la respuesta.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = getTokenFromRequest(request);

            if (token != null) {
                String email = jwtUtils.getUsernameFromToken(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                if (StringUtils.hasText(email) && jwtUtils.isTokenValid(token, userDetails)) {
                    log.info("Token is valid, {}", email);

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception e) {
            log.error("Error en AuthFilter al validar token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"estatus\": 401, \"mensaje\": \"No autorizado: " + e.getMessage() + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del encabezado {@code Authorization} de la petición HTTP.
     *
     * @param request solicitud HTTP entrante.
     * @return la cadena del token JWT sin el prefijo "Bearer ", o {@code null} si el encabezado no está presente o no tiene el formato adecuado.
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String tokenWithBearer = request.getHeader("Authorization");
        if (tokenWithBearer != null && tokenWithBearer.startsWith("Bearer ")) {
            return tokenWithBearer.substring(7);
        }
        return null;
    }

}