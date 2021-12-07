package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.User.UserGetResponseDto;
import com.around.wmmarket.controller.dto.User.UserSaveRequestDto;
import com.around.wmmarket.controller.dto.User.UserSigninRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.user.CustomUserDetailsService;
import com.around.wmmarket.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserApiController {
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/api/v1/user")
    public ResponseEntity<?> save(@RequestBody UserSaveRequestDto requestDto){
        if(userService.isExist(requestDto.getEmail())) return ResponseEntity.badRequest().body("중복된 아이디가 있습니다.");
        userService.save(requestDto);
        return ResponseEntity.ok().body("회원가입 성공");
    }

    @Transactional
    @PostMapping("/api/v1/user/signIn")
    public ResponseEntity<?> signIn(@RequestBody UserSigninRequestDto requestDto, HttpSession session){
        // 이미 로그인한 유저면 반환
        if(session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)!=null){
            return ResponseEntity.badRequest().body("이미 로그인하셨습니다!");
        }
        SignedUser signedUser;
        try{
            signedUser = customUserDetailsService.getSignedUser(requestDto);
        }catch (Exception e){
            //e.printStackTrace();
            return ResponseEntity.badRequest().body("아이디를 확인해주세요");
        }
        // 인증 토큰 발급
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(signedUser.getUsername(),signedUser.getPassword());
        // 인증 객체
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(token);
        }catch (Exception e){
            //e.printStackTrace();
            //throw e;
            return ResponseEntity.badRequest().body("비밀번호를 확인해주세요!");
        }
        // 시큐리티 컨텍스트에 인증 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 세션에 컨텍스트 저장
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,SecurityContextHolder.getContext());
        // 로그인 유저에 토큰 저장
        //signedUser.setTokenId(RequestContextHolder.currentRequestAttributes().getSessionId());
        return ResponseEntity.ok().body("로그인 성공!");
    }

    @GetMapping("/api/v1/user/isExist")
    public ResponseEntity<Boolean> checkDuplicate(@RequestParam String email){
        return ResponseEntity.ok().body(userService.isExist(email));
    }

    @GetMapping("/api/v1/user")
    public ResponseEntity<?> getUser(@RequestParam String email){
        if(!userService.isExist(email)) return ResponseEntity.badRequest().body("유저가 존재하지 않습니다.");
        UserGetResponseDto responseDto = userService.getUser(email);
        return ResponseEntity.ok().body(responseDto);
    }
}
