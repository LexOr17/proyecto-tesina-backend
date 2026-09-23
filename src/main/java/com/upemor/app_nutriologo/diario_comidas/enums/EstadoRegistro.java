package com.upemor.app_nutriologo.diario_comidas.enums;

/**
 * Enumeración que representa el estado de cumplimiento de una comida registrada en el diario.
 * <p>
 * Permite conocer si el paciente consumió exactamente la comida planificada, si realizó
 * modificaciones en los alimentos consumidos, si omitió la comida o si no realizó el registro a tiempo.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public enum EstadoRegistro {
    /**
     * El paciente consumió exactamente lo indicado en el plan alimenticio.
     */
    CUMPLIDO,

    /**
     * El paciente consumió alimentos distintos o cantidades diferentes a las sugeridas en el plan.
     */
    MODIFICADO,

    /**
     * El paciente indicó explícitamente que no realizó la comida (por ayuno u otro motivo).
     */
    SALTADA,

    /**
     * El sistema detecta que el plazo de gracia para registrar la comida venció sin interacción del paciente.
     */
    NO_REGISTRADO
}