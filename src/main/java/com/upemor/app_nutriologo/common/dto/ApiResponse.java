package com.upemor.app_nutriologo.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor // <-- Esto crea el constructor público con todos los argumentos
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // No incluye campos nulos en el JSON
public class ApiResponse<T> {

    private int estatus;
    private String mensaje;
    private T datos;

    // --- MÉTODOS ESTÁTICOS DE AYUDA ---

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .estatus(HttpStatus.OK.value())
                .mensaje(message)
                .datos(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .estatus(HttpStatus.CREATED.value())
                .mensaje(message)
                .datos(data)
                .build();
    }

    public static ApiResponse<Void> noContent(String message) {
        return ApiResponse.<Void>builder()
                .estatus(HttpStatus.NO_CONTENT.value())
                .mensaje(message)
                .build();
    }
}