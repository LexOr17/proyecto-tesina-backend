package com.upemor.app_nutriologo.historial_medico.dto;

import com.upemor.app_nutriologo.historial_medico.enums.EnfermedadEnum;
import com.upemor.app_nutriologo.historial_medico.enums.EstadoMedicamento;
import com.upemor.app_nutriologo.historial_medico.enums.SintomaEnum;
import com.upemor.app_nutriologo.medidas_antropometricas.dto.MedidaAntropometricaResponseDTO;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record HistorialMedicoResponseDTO(

        Long id,

        // ─── Enfermedades ──────────────────────────────────────────────────────
        List<EnfermedadEnum> enfermedades,
        String otraEnfermedad,

        // ─── Alergias ──────────────────────────────────────────────────────────
        String alergias,

        // ─── Síntomas ──────────────────────────────────────────────────────────
        List<SintomaEnum> sintomas,
        String otroSintoma,

        // ─── Medicamentos / Suplementos ────────────────────────────────────────
        EstadoMedicamento estadoMedicamento,
        String descripcionMedicamento,

        // ─── Auditoría y Relaciones ────────────────────────────────────────────
        LocalDateTime fechaRegistro,
        Long pacienteId,

        // Medidas antropométricas vinculadas a este historial (puede ser null)
        MedidaAntropometricaResponseDTO medidaAntropometrica
) {}
