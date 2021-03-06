package com.around.wmmarket.controller.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@Builder
public class UserSignInRequestDto {
    @ApiModelProperty(value = "유저 이메일",example = "test_email@gmail.com",required = true)
    @NotBlank @Email
    private final String email;
    @ApiModelProperty(value = "유저 비밀번호",example = "test_password",required = true)
    @NotBlank
    private final String password;
}
