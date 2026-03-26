package com.upemor.app_nutriologo.meta.dto;

public record MetaStatusCountDTO(
        long pendientes,
        long porValidar,
        long cumplidas,
        long rechazadas,
        long noCumplidas,
        long total,
        double porcentajeExito
) {
}
