package com.upemor.app_nutriologo.meta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MetaResponseDTO {

    private Integer id;

    private String descripcion;

    @JsonProperty("fecha_objetivo")
    private LocalDate fechaObjetivo;

    private boolean cumplida;

    @JsonProperty("paciente_id")
    private Long pacienteId;

    @JsonProperty("nutriologo_id")
    private Long nutriologoId;
}
