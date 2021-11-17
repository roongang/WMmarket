package com.around.wmmarket.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSigninRequestDto {
    private String email;
    private String password;

    @Builder
    public UserSigninRequestDto(String email,String password){
        this.email=email;
        this.password=password;
    }
}
