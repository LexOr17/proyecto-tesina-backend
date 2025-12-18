package com.upemor.app_nutriologo.correo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
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

    @Override
    public void sendEmail(String[] destinatario, String asunto, String contenido) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("lexhar13@gmail.com");
        mailMessage.setTo(destinatario);
        mailMessage.setSubject(asunto);
        mailMessage.setText(contenido);

        mailSender.send(mailMessage);
    }

    @Override
    public void sendEmailWithFile(String[] destinatario, String asunto, String contenido, File archivo) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom("lexhar13@gmail.com");
            mimeMessageHelper.setTo(destinatario);
            mimeMessageHelper.setSubject(asunto);
            mimeMessageHelper.setText(contenido);
            mimeMessageHelper.addAttachment(archivo.getName(), archivo);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
