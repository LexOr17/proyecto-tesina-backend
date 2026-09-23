package com.upemor.app_nutriologo.security;

import com.upemor.app_nutriologo.common.exceptions.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Componente utilitario para la generación, análisis y validación de tokens JWT (JSON Web Tokens).
 * <p>
 * Gestiona el ciclo de vida de los tokens de autenticación utilizados en la API,
 * aplicando el algoritmo criptográfico HMAC-SHA256 para la firma y verificación de integridad.
 * </p>
 * 
 * @author Sistema Nutriólogo
 * @version 1.0
 */
@Service
@Slf4j
public class JwtUtils {

    /**
     * Tiempo de expiración del token configurado en 8 horas (en milisegundos).
     */
    private static final long EXPIRATION_TIME_IN_MILLISEC = 1000L * 60L * 60L * 8L; // expira en 8 horas

    /**
     * Clave criptográfica HMAC-SHA256 empleada para firmar y validar tokens.
     */
    private SecretKey key;

    /**
     * Cadena secreta inyectada desde las propiedades de la aplicación ({@code secreteJwtString}).
     */
    @Value("${secreteJwtString}")
    private String secreteJwtString;

    /**
     * Inicializa la clave criptográfica {@link SecretKey} a partir de la cadena secreta
     * una vez completada la inyección de dependencias.
     */
    @PostConstruct
    private void init() {
        byte[] keyByte = secreteJwtString.getBytes(StandardCharsets.UTF_8);
        this.key = new SecretKeySpec(keyByte, "HmacSHA256");
    }

    /**
     * Genera un nuevo token JWT firmado para el usuario especificado.
     * <p>
     * Incluye en el payload el rol del usuario (sin el prefijo {@code ROLE_}),
     * el nombre de usuario (correo) como subject, la fecha de emisión (issuedAt)
     * y la fecha de expiración (8 horas a partir del momento de creación).
     * </p>
     *
     * @param userDetails detalles del usuario autenticado ({@link UserDetails}).
     * @return cadena compacta que representa el token JWT firmado.
     * @throws UnauthorizedException si el usuario no tiene ninguna autoridad o rol asignado.
     */
    public String generateToken(UserDetails userDetails) {

        String rol = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority() != null ? a.getAuthority().replace("ROLE_", "") : "")
                .orElseThrow(() -> new UnauthorizedException("El usuario no tiene un rol asignado."));

        return Jwts.builder()
                .claim("rol", rol) // IMPORTANTE
                // .claim("nombre", usuario.getNombre())
                // .claim("apellido", usuario.getApellidoPaterno())
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLISEC))
                .signWith(key)
                .compact();
    }

    /**
     * Extrae el nombre de usuario (correo electrónico) almacenado en el claim {@code subject} del token.
     *
     * @param token cadena del token JWT.
     * @return nombre de usuario o correo extraído del token.
     */
    public String getUsernameFromToken(String token) {
        return extractClaims(token, claims -> claims.getSubject());
    }

    /**
     * Método auxiliar genérico para extraer información específica de los claims del token.
     *
     * @param <T> tipo de dato retornado por la función extractora.
     * @param token cadena del token JWT.
     * @param claimsTFunction función resolutora que obtiene el valor deseado de los {@link Claims}.
     * @return el resultado de aplicar la función extractora sobre el payload de claims.
     */
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
    }

    /**
     * Valida si un token JWT es válido para un determinado usuario.
     * <p>
     * Comprueba que el nombre de usuario del token coincida con el de {@link UserDetails}
     * y que el token no haya superado su fecha de expiración.
     * </p>
     *
     * @param token cadena del token JWT a evaluar.
     * @param userDetails detalles del usuario contra los cuales se valida el token.
     * @return {@code true} si el token es válido y vigente; {@code false} en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Comprueba si el token JWT ha expirado en comparación con la fecha y hora actual.
     *
     * @param token cadena del token JWT.
     * @return {@code true} si la fecha de expiración es anterior a la fecha actual; {@code false} en caso contrario.
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token, claims -> claims.getExpiration()).before(new Date());
    }

}

