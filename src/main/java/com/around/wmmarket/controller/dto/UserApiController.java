package com.around.wmmarket.controller.dto;

import com.around.wmmarket.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserApiController {
    private UserService userService;

    @PutMapping("/api/v1/user")
    public void save(@RequestBody UserSaveRequestDto requestDto){
        userService.signUp(requestDto);
    }
}
