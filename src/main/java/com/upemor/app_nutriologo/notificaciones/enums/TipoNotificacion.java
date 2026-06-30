package com.upemor.app_nutriologo.notificaciones.enums;

/**
 * Define los tipos de notificaciones disponibles en el sistema.
 * Extender este enum para agregar nuevos tipos en el futuro.
 */
public enum TipoNotificacion {

    /**
     * Se dispara cuando se crea una nueva plantilla alimenticia.
     * Destinatarios: todos los usuarios EXCEPTO quien la creó.
     */
    NUEVA_PLANTILLA,

    /**
     * Se dispara cuando el administrador registra un paciente y lo asigna a un nutriólogo.
     * Destinatario: el nutriólogo asignado (no el administrador que lo registró).
     */
    PACIENTE_ASIGNADO
}
