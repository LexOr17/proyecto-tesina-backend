package com.upemor.app_nutriologo.historial_medico.dto;

import com.upemor.app_nutriologo.historial_medico.enums.EnfermedadEnum;
import com.upemor.app_nutriologo.historial_medico.enums.EstadoMedicamento;
import com.upemor.app_nutriologo.historial_medico.enums.SintomaEnum;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record HistorialMedicoRequestDTO(

        // ─── Enfermedades ──────────────────────────────────────────────────────

        // Lista de enfermedades conocidas (selección múltiple, puede ser vacía)
        List<EnfermedadEnum> enfermedades,

        // Descripción libre cuando se incluye EnfermedadEnum.OTRA en la lista (validado en el servicio)
        String otraEnfermedad,

        // ─── Alergias ──────────────────────────────────────────────────────────

        // Alergias conocidas del paciente (texto libre, opcional)
        String alergias,

        // ─── Síntomas ──────────────────────────────────────────────────────────

        // Lista de síntomas actuales (selección múltiple, puede ser vacía)
        List<SintomaEnum> sintomas,

        // Descripción libre cuando se incluye SintomaEnum.OTRO en la lista (validado en el servicio)
        String otroSintoma,

        // ─── Medicamentos / Suplementos ────────────────────────────────────────

        // Estado del paciente: NUNCA / ACTUALMENTE / ANTERIORMENTE
        @NotNull(message = "Debe indicar el estado respecto al consumo de medicamentos o suplementos")
        EstadoMedicamento estadoMedicamento,

        // Nombre, dosis y frecuencia. Obligatorio cuando estadoMedicamento != NUNCA (validado en el servicio)
        String descripcionMedicamento,

        // ─── Relación ──────────────────────────────────────────────────────────

        // ID de la medida antropométrica asociada (opcional, debe pertenecer al mismo paciente)
        Long medidaAntropometricaId
) {}
