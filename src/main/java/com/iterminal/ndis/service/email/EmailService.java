
package com.iterminal.ndis.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("spring.mail.username")
    private String mailUsername;

    public void send(String to, String subject, String body, String htmlText) throws RuntimeException {
        sendHTML(to, subject, body, htmlText);
    }

    public void sendHTML(String to, String subject, String body, String htmlText) {
        MimeMessagePreparator mailMessage = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(
                    mimeMessage, true, "UTF-8");
            message.setFrom(mailUsername, " iDoc Document Management System");
            message.setTo(to);
            message.setSubject(subject);

            if (htmlText == null) {
                message.setText(body);
            } else {
                String htmlTextNew = htmlText.replace("{{systemName}}", "iDoc DMS");
                message.setText(body, htmlTextNew);
            }
        };
        javaMailSender.send(mailMessage);
    }
}
