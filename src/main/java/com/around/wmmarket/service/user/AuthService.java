package com.around.wmmarket.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final MailSender mailSender;

    public void sendMail(){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("iam.wmmarket@gmail.com");
        message.setSubject("[인증]WM-market 회원 인증");
        message.setText("인증번호다 임마");
        message.setTo("gusgh3315@gmail.com");
        mailSender.send(message);
    }
}
