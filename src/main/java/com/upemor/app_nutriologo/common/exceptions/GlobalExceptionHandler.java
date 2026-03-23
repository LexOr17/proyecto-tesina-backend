package com.upemor.app_nutriologo.common.exceptions;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errores = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("Error de validación: {}", errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .estatus(HttpStatus.BAD_REQUEST.value())
                        .mensaje("Error de validación")
                        .datos(errores)
                        .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("Recurso no encontrado: {}", e.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .estatus(HttpStatus.NOT_FOUND.value())
                .mensaje(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException e) {
        log.warn("Solicitud inválida: {}", e.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .estatus(HttpStatus.BAD_REQUEST.value())
                .mensaje(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("Acceso no autorizado: {}", e.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .estatus(HttpStatus.UNAUTHORIZED.value())
                .mensaje(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(DuplicateResourceException e) {
        log.warn("Recurso duplicado: {}", e.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .estatus(HttpStatus.CONFLICT.value())
                .mensaje(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ScheduleConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleScheduleConflictException(ScheduleConflictException e) {
        log.warn("Conflicto de horario: {}", e.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .estatus(HttpStatus.CONFLICT.value())
                .mensaje(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidOperationException(InvalidOperationException e) {
        log.warn("Operación inválida: {}", e.getMessage());
        ApiResponse<Object> response = ApiResponse.builder()
                .estatus(HttpStatus.CONFLICT.value())
                .mensaje(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception e) {
        log.error("Error inesperado: ", e);
        ApiResponse<Object> response = ApiResponse.builder()
                .estatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .mensaje("Error interno del servidor. Por favor, intenta más tarde.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException e) {
        ApiResponse<Object> response = ApiResponse.builder()
                .estatus(HttpStatus.UNAUTHORIZED.value())
                .mensaje("Correo o contraseña incorrectos.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        log.warn("Acceso denegado: {}", e.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .estatus(HttpStatus.FORBIDDEN.value())
                .mensaje(e.getMessage())
                // .datos(null) // Opcional, ya que el builder suele dejarlo null por defecto
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}
