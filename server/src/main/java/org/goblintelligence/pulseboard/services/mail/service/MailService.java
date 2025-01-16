package org.goblintelligence.pulseboard.services.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender sender;

    public void sendMessage(String text, String subject, String... sendTo) {
        sender.send(formMessage(text, subject, sendTo));
    }

    private SimpleMailMessage formMessage(String text, String subject, String... sendTo) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setSubject(subject);
        message.setText(text);
        message.setFrom(fromEmail);
        message.setTo(sendTo);

        return message;
    }
}
