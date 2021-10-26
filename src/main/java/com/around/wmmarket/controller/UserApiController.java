package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.UserLoginRequestDto;
import com.around.wmmarket.controller.dto.UserSaveRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.user.CustomUserDetailsService;
import com.around.wmmarket.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@RestController
public class UserApiController {
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;

    @PutMapping("/api/v1/user")
    public void save(@RequestBody UserSaveRequestDto requestDto){
        userService.save(requestDto);
    }

    @PostMapping("/api/v1/user/signIn")
    public ResponseEntity<?> signIn(@RequestBody UserLoginRequestDto requestDto, HttpSession session){
        SignedUser signedUser = customUserDetailsService.getSignedUser(requestDto,session);
        // 인증 토큰 발급
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(requestDto.getUsername(),requestDto.getPassword());;
        // 인증 객체
        Authentication authentication = authenticationManager.authenticate(token);
        // 시큐리티 컨텍스트에 인증 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 세션에 컨텍스트 저장
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,SecurityContextHolder.getContext());
        // 로그인 유저에 토큰 저장
        signedUser.setTokenId(RequestContextHolder.currentRequestAttributes().getSessionId());
        return ResponseEntity.ok(signedUser);
    }
}
