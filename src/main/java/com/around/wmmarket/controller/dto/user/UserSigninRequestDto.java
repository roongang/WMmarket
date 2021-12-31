package com.around.wmmarket.controller.dto.user;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSigninRequestDto {
    @ApiModelProperty(value="이메일",example = "test_email@gmail.com",required = true)
    private String email;
    @ApiModelProperty(value = "비밀번호",example = "test_password",required = true)
    private String password;

    @Builder
    public UserSigninRequestDto(String email,String password){
        this.email=email;
        this.password=password;
    }
}
