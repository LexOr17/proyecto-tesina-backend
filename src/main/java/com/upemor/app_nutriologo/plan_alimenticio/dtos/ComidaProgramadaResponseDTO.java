package com.upemor.app_nutriologo.plan_alimenticio.dtos;

import com.upemor.app_nutriologo.plantillas.enums.TipoComida;
import lombok.Data;

@Data
public class ComidaProgramadaResponseDTO {
    private Long id;
    private Long plantillaId;
    private String plantillaNombre; // ¡MapStruct nos puede traer el nombre de la plantilla automáticamente!
    private Integer diaPlan;
    private TipoComida horario;
    private String observaciones;
}