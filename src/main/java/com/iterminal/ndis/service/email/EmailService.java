
/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
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
            message.setFrom(mailUsername, " National Development Information System");
            message.setTo(to);
            message.setSubject(subject);
            if (htmlText == null) {
                message.setText(body);
            } else {
                message.setText(body, htmlText);
            }
        };
        javaMailSender.send(mailMessage);
    }
}
