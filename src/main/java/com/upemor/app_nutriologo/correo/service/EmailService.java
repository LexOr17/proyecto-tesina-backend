package com.upemor.app_nutriologo.correo.service;

import org.springframework.scheduling.annotation.Async;

import java.io.File;

public interface EmailService {

    @Async
    void sendEmail(String[] destinatario, String asunto, String contenido);

    void sendEmailWithFile(String[] destinatario, String asunto, String contenido, File archivo);

}
