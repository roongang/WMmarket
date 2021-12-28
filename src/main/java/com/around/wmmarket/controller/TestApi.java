package com.around.wmmarket.controller;

import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.controller.dto.user.UserGetResponseDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.around.wmmarket.common.error.ErrorCode.MEMBER_NOT_FOUND;

@RestController
public class TestApi {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/get/test")
    public ResponseEntity<Object> getTest(@AuthenticationPrincipal SignedUser signedUser){
        try{
            // error
            // userService.getUser(signedUser.getUsername());

            UserGetResponseDto responseDto=UserGetResponseDto.builder()
                    .email("user_email")
                    .nickname("user_nickname")
                    .build();
            return ResponseHandler.toResponse("TEST SUCCESS", HttpStatus.OK,responseDto);
        } catch (Exception e){
            // 이렇게 에러를 던지면 message 만 받음.
            return ResponseHandler.toResponse(e.getMessage(),HttpStatus.MULTI_STATUS,null);
        }
    }

    @GetMapping("/get/test/2")
    public ResponseEntity<Object> getTest2(@AuthenticationPrincipal SignedUser signedUser) throws Exception{
        try{
            // error
            userService.getUser(signedUser.getUsername());

            UserGetResponseDto responseDto=UserGetResponseDto.builder()
                    .email("user_email")
                    .nickname("user_nickname")
                    .build();
            return ResponseHandler.toResponse("TEST SUCCESS", HttpStatus.OK,responseDto);
        } catch (Exception e){
            // 이렇게 에러를 던지면 message 만 받음.
            return ResponseHandler.toResponse(e.getMessage(),HttpStatus.MULTI_STATUS,null);
        }
    }

    @GetMapping("/get/test/3")
    public ResponseEntity<Object> getTest3(){
        User user=userRepository.findById(1)
                .orElseThrow(()->new CustomException(MEMBER_NOT_FOUND));

        return ResponseHandler.toResponse("TEST SUCCESS",HttpStatus.OK,user.getEmail());
    }
}
