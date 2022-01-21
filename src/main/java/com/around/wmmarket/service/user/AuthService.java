package com.around.wmmarket.service.user;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final MailSender mailSender;
    private final UserRepository userRepository;

    @Async
    public void sendAuthCode(String email){
        // check
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        if(user.getIsAuth()!=0) throw new CustomException(ErrorCode.DUPLICATED_USER_AUTH);
        // send email
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("iam.wmmarket@gmail.com");
        message.setSubject("[회원인증] WM-market 회원 인증 이메일입니다.");
        message.setText(String.format("사랑합니다 고객님\n"
                +"수박맛 중고거래 수박 마켓입니다\n"
                +"이메일 인증을 통해 소중한 수박 마켓의 정회원이 되어주세요!\n"
                +"인증번호 : %s\n" , getAuthCode(user)));
        message.setTo(user.getEmail());
        mailSender.send(message);
    }
    private String getAuthCode(User user){
        String authCode=generateCode();
        user.setCode(authCode);
        return authCode;
    }
    private String generateCode(){
        byte[] bytes=new byte[4];
        new Random().nextBytes(bytes);
        StringBuilder builder=new StringBuilder();
        for(byte b:bytes){
            builder.append(String.format("%02x",b));
        }
        return builder.toString();
    }
}
