package com.around.wmmarket.service.user;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.user.UserSignInRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Service
public class SignService {
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;

    public void signin(UserSignInRequestDto requestDto, HttpSession session){
        // 이미 로그인한 유저면 반환
        if(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)!=null) throw new CustomException(ErrorCode.DUPLICATED_SIGN_IN);

        SignedUser signedUser;
        try { signedUser = customUserDetailsService.getSignedUser(requestDto);}
        catch (UsernameNotFoundException e) {
            session.invalidate();
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        // 인증 객체
        Authentication authentication;
        try{ authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        signedUser.getUsername()
                        ,signedUser.getPassword()));}
        catch (Exception e){
            session.invalidate();
            throw new CustomException(ErrorCode.INVALID_USER_PASSWORD);
        }

        // 시큐리티 컨텍스트에 인증 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 세션에 컨텍스트 저장
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
                ,SecurityContextHolder.getContext());


    }
    public void signout(HttpSession session){
        if(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)==null){
            throw new CustomException(ErrorCode.SIGNED_USER_NOT_FOUND);
        }
        session.invalidate();
    }
}
