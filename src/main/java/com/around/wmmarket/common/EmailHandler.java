package com.around.wmmarket.common;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailHandler {
    private final MailSender mailSender;

    @Async
    public void sendSimpleEmail(String fromEmail,String toEmail,String subject,String text){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setSubject(subject);
        message.setText(text);
        message.setTo(toEmail);
        mailSender.send(message);
    }
}
