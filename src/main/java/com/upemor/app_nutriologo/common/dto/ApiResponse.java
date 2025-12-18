package com.upemor.app_nutriologo.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private String mensaje;
    private int  estatus;
    private T datos = null;
    private final LocalDateTime timestamp = LocalDateTime.now();

}
