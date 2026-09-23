package com.upemor.app_nutriologo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Properties;

/**
 * Configuración para el servicio de envío de correos electrónicos de la aplicación.
 * <p>
 * Habilita la ejecución asíncrona mediante {@link EnableAsync} para enviar correos sin bloquear
 * el hilo principal de las solicitudes HTTP, y provee el bean {@link JavaMailSender} configurado
 * para conectarse al servidor SMTP de Gmail.
 * </p>
 * 
 * @author Sistema Nutriólogo
 * @version 1.0
 * @see JavaMailSender
 * @see EnableAsync
 */
@Configuration
@EnableAsync
public class MailConfiguration {

    /**
     * Crea y configura el bean {@link JavaMailSender} con los parámetros de conexión SMTP.
     * <p>
     * Establece el host ({@code smtp.gmail.com}), puerto (587), credenciales de autenticación
     * y propiedades de transporte seguro (STARTTLS y autenticación activada).
     * </p>
     *
     * @return instancia configurada de {@link JavaMailSender} para el envío de correos.
     */
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("lexhar13@gmail.com");
        mailSender.setPassword("bmopdoipblhkbcji");

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "true");

        return mailSender;
    }

}

