package com.upemor.app_nutriologo.correo.service;

import com.upemor.app_nutriologo.common.exceptions.MailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.sender}")
    private String emailSender;

    @Override
    public void sendEmail(String[] destinatario, String asunto, String contenido) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailSender);
        mailMessage.setTo(destinatario);
        mailMessage.setSubject(asunto);
        mailMessage.setText(contenido);

        mailSender.send(mailMessage);
    }

    @Override
    public void sendHtmlEmail(String[] destinatario, String asunto, String contenidoHtml) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            helper.setFrom(emailSender);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenidoHtml, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailSendException("Error al enviar correo electrónico HTML", e);
        }
    }

    @Override
    public void sendEmailWithFile(String[] destinatario, String asunto, String contenido, File archivo) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(emailSender);
            mimeMessageHelper.setTo(destinatario);
            mimeMessageHelper.setSubject(asunto);
            mimeMessageHelper.setText(contenido);
            mimeMessageHelper.addAttachment(archivo.getName(), archivo);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new MailSendException("Error al enviar correo electrónico con archivo adjunto", e);
        }
    }

}
