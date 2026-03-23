package com.upemor.app_nutriologo.diario_comidas.enums;

public enum EstadoRegistro {
    CUMPLIDO,          // Comió exactamente lo que decía el plan
    MODIFICADO,        // Comió algo diferente a lo sugerido
    SALTADA,           // El paciente indicó que no realizó esa comida (ayuno, etc.)
    NO_REGISTRADO      // El sistema detecta que el plazo de gracia venció sin acción
}