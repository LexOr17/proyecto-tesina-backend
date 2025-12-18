package com.upemor.app_nutriologo.correo.service;

import java.io.File;

public interface EmailService {

    void sendEmail(String[] destinatario, String asunto, String contenido);

    void sendEmailWithFile(String[] destinatario, String asunto, String contenido, File archivo);

}
