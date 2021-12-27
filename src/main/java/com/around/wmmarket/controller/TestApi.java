package com.around.wmmarket.controller;

import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.controller.dto.user.UserGetResponseDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestApi {
    @Autowired
    UserService userService;

    @GetMapping("/get/test")
    public ResponseEntity<Object> getTest(@AuthenticationPrincipal SignedUser signedUser){
        try{
            // error
            userService.getUser(signedUser.getUsername());

            UserGetResponseDto responseDto=UserGetResponseDto.builder()
                    .email("user_email")
                    .nickname("user_nickname")
                    .build();
            return ResponseHandler.generateResponse("TEST SUCCESS", HttpStatus.OK,responseDto);
        } catch (Exception e){
            // 이렇게 에러를 던지면 message 만 받음.
            return ResponseHandler.generateResponse(e.getMessage(),HttpStatus.MULTI_STATUS,null);
        }
    }

    @GetMapping("/get/test/2")
    public ResponseEntity<Object> getTest2(@AuthenticationPrincipal SignedUser signedUser) throws Exception{
        userService.getUser(signedUser.getUsername());

        UserGetResponseDto responseDto=UserGetResponseDto.builder()
                .email("user_email")
                .nickname("user_nickname")
                .build();
        return ResponseHandler.generateResponse("TEST SUCCESS", HttpStatus.OK,responseDto);
    }
}
