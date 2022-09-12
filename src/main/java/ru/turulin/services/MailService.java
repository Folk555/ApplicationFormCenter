package ru.turulin.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Value("${spring.mail.username}")
    private String nameSMTPServer;
    @Autowired
    private JavaMailSender javaEmailSender;

    public void sendMail(
            String mailTo, String subject, String message) {
        SimpleMailMessage MailMessage = new SimpleMailMessage();
        MailMessage.setFrom(nameSMTPServer+"@yandex.ru");
        MailMessage.setTo(mailTo);
        MailMessage.setSubject(subject);
        MailMessage.setText(message);
        javaEmailSender.send(MailMessage);
    }
}
