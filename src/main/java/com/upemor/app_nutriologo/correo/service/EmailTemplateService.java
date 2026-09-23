package com.upemor.app_nutriologo.correo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final TemplateEngine templateEngine;

    /**
     * Renderiza el template de credenciales de nuevo usuario.
     *
     * @param nombre   Nombre del usuario
     * @param correo   Correo electrónico del usuario
     * @param password Contraseña generada
     * @return HTML renderizado listo para enviar por email
     */
    public String renderCredencialesEmail(String nombre, String correo, String password) {
        Context context = new Context();
        context.setVariable("nombre", nombre);
        context.setVariable("correo", correo);
        context.setVariable("password", password);

        return templateEngine.process("email/credenciales-email", context);
    }

    /**
     * Renderiza el template de recuperación de contraseña.
     *
     * @param nombre   Nombre del usuario
     * @param password Nueva contraseña generada
     * @return HTML renderizado listo para enviar por email
     */
    public String renderRecuperacionEmail(String nombre, String password) {
        Context context = new Context();
        context.setVariable("nombre", nombre);
        context.setVariable("password", password);

        return templateEngine.process("email/recuperacion-email", context);
    }

    /**
     * Renderiza el template de cita confirmada/programada para el paciente.
     *
     * @param nombrePaciente   Nombre del paciente
     * @param nombreNutriologo Nombre del nutriólogo
     * @param fechaHoraInicio  Fecha y hora de inicio formateada
     * @param fechaHoraFin     Hora de fin formateada (opcional)
     * @param motivoCita       Descripción del motivo de la cita
     * @return HTML renderizado listo para enviar por email
     */
    public String renderCitaConfirmadaEmail(String nombrePaciente, String nombreNutriologo,
                                           String fechaHoraInicio, String fechaHoraFin, String motivoCita) {
        Context context = new Context();
        context.setVariable("nombrePaciente", nombrePaciente);
        context.setVariable("nombreNutriologo", nombreNutriologo);
        context.setVariable("fechaHoraInicio", fechaHoraInicio);
        context.setVariable("fechaHoraFin", fechaHoraFin);
        context.setVariable("motivoCita", motivoCita);

        return templateEngine.process("email/cita-confirmada-email", context);
    }

    /**
     * Renderiza el template de cita cancelada para el paciente.
     *
     * @param nombrePaciente    Nombre del paciente
     * @param fechaHoraInicio   Fecha y hora original formateada
     * @param motivoCita        Motivo original de la cita
     * @param motivoCancelacion Justificación de la cancelación (si aplica)
     * @return HTML renderizado listo para enviar por email
     */
    public String renderCitaCanceladaEmail(String nombrePaciente, String fechaHoraInicio,
                                          String motivoCita, String motivoCancelacion) {
        Context context = new Context();
        context.setVariable("nombrePaciente", nombrePaciente);
        context.setVariable("fechaHoraInicio", fechaHoraInicio);
        context.setVariable("motivoCita", motivoCita);
        context.setVariable("motivoCancelacion", motivoCancelacion);

        return templateEngine.process("email/cita-cancelada-email", context);
    }

    /**
     * Renderiza el template de nueva solicitud de cita para el nutriólogo.
     *
     * @param nombreNutriologo Nombre del nutriólogo
     * @param nombrePaciente   Nombre del paciente solicitante
     * @param fechaHoraInicio  Fecha y hora solicitada formateada
     * @param motivoCita       Motivo de la cita
     * @param detalleAdicional Notas o síntomas adicionales
     * @return HTML renderizado listo para enviar por email
     */
    public String renderCitaSolicitadaEmail(String nombreNutriologo, String nombrePaciente,
                                           String fechaHoraInicio, String motivoCita, String detalleAdicional) {
        Context context = new Context();
        context.setVariable("nombreNutriologo", nombreNutriologo);
        context.setVariable("nombrePaciente", nombrePaciente);
        context.setVariable("fechaHoraInicio", fechaHoraInicio);
        context.setVariable("motivoCita", motivoCita);
        context.setVariable("detalleAdicional", detalleAdicional);

        return templateEngine.process("email/cita-solicitada-email", context);
    }
}
