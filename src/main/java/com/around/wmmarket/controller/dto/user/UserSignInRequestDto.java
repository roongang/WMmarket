package com.around.wmmarket.controller.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class UserSignInRequestDto {
    @ApiModelProperty(value = "유저 이메일",example = "test_email@gmail.com",required = true)
    private String email;
    @ApiModelProperty(value = "유저 비밀번호",example = "test_password",required = true)
    private String password;
}
