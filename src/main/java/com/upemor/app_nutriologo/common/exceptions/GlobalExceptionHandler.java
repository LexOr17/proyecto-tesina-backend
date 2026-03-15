package com.upemor.app_nutriologo.common.exceptions;

import com.upemor.app_nutriologo.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errores = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .estatus(HttpStatus.BAD_REQUEST.value())
                        .mensaje("Error de validación")
                        .datos(errores)
                        .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException e) {
        ApiResponse<Object> response = ApiResponse.builder()
                .estatus(HttpStatus.NOT_FOUND.value())
                .mensaje(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(DuplicateResourceException e) {
        ApiResponse<Object> response = ApiResponse.builder()
                .estatus(HttpStatus.CONFLICT.value())
                .mensaje(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ScheduleConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleScheduleConflictException(ScheduleConflictException e) {
        ApiResponse<Object> response = ApiResponse.builder()
                .estatus(HttpStatus.CONFLICT.value())
                .mensaje(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception e) {
        ApiResponse<Object> response = ApiResponse.builder()
                .estatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .mensaje(e.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
