package com.upemor.app_nutriologo.notificaciones.enums;

/**
 * Define los tipos de notificaciones disponibles en el sistema.
 * Extender este enum para agregar nuevos tipos en el futuro.
 */
public enum TipoNotificacion {

    /**
     * Se dispara cuando se crea una nueva plantilla alimenticia.
     * Destinatarios: todos los usuarios activos EXCEPTO quien la creó.
     */
    NUEVA_PLANTILLA,

    /**
     * Se dispara cuando el administrador registra un paciente y lo asigna a un nutriólogo.
     * Destinatario: el nutriólogo asignado.
     */
    PACIENTE_ASIGNADO,

    /**
     * Se dispara cuando un nutriólogo asigna un plan alimenticio a un paciente.
     * Destinatario: el paciente al que se le asigna el plan.
     */
    PLAN_ASIGNADO,

    /**
     * Se dispara cuando se registra un nuevo alimento en el catálogo.
     * Destinatarios: nutriólogos y pacientes activos EXCEPTO quien lo registró.
     */
    NUEVO_ALIMENTO,

    /**
     * Se dispara cuando un paciente solicita una nueva cita.
     * Destinatario: el nutriólogo asignado.
     */
    CITA_SOLICITADA,

    /**
     * Se dispara cuando una cita es aceptada, programada o cancelada.
     * Destinatario: el paciente o nutriólogo según corresponda.
     */
    CITA_ACTUALIZADA,

    /**
     * Se dispara cuando se recibe un nuevo mensaje instantáneo por chat.
     * Destinatario: el usuario receptor del mensaje.
     */
    NUEVO_MENSAJE_CHAT,

    /**
     * Se dispara cuando un paciente envía una nueva sugerencia sobre plantillas.
     * Destinatario: el nutriólogo asignado.
     */
    NUEVA_SUGERENCIA,

    /**
     * Se dispara cuando el nutriólogo revisa (aprueba o rechaza) una sugerencia.
     * Destinatario: el paciente que realizó la sugerencia.
     */
    SUGERENCIA_REVISADA
}
