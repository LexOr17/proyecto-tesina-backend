package com.upemor.app_nutriologo.usuario.enums;

/**
 * Enumeración que define los diferentes roles de usuario disponibles en el sistema.
 * <p>
 * Cada rol determina el nivel de acceso, permisos y capacidades operativas
 * dentro de la aplicación.
 * </p>
 *
 * @author Sistema Nutriólogo
 * @version 1.0
 */
public enum Rol {

    /**
     * Rol con privilegios globales de administración, gestión de usuarios y configuración del sistema.
     */
    ADMINISTRADOR,

    /**
     * Rol para profesionales de nutrición, con permisos para gestionar pacientes, planes alimenticios, consultas y citas.
     */
    NUTRIOLOGO,

    /**
     * Rol para pacientes o clientes finales, con permisos para consultar sus planes, registrar diarios y agendar citas.
     */
    PACIENTE
}
