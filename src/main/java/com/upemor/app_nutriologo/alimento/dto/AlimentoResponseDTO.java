package com.upemor.app_nutriologo.alimento.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlimentoResponseDTO {

    private Long id;
    private String nombre;
    private String categoria;
    private BigDecimal calorias;
    private BigDecimal proteina;
    private BigDecimal carbohidratos;
    private BigDecimal grasas;
    private BigDecimal porcionBase;
    private String unidadPorcion;
    private String imagenUrl;
    private LocalDate fechaRegistro;
    private String registradoPor; // Nombre o correo del usuario que lo registró
}
