package com.upemor.app_nutriologo.meta.enums;

public enum EstadoMeta {
    PENDIENTE,      // La meta está activa pero el paciente no ha reportado nada
    POR_VALIDAR,    // El paciente marcó que la cumplió y espera al nutriólogo
    CUMPLIDA,       // El nutriólogo validó el cumplimiento
    RECHAZADA,      // El nutriólogo consideró que no se cumplió (opcional)
    NO_CUMPLIDA     // El paciente no cumplió la meta
}
