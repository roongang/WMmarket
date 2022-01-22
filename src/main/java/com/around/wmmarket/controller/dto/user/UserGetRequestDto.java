package com.around.wmmarket.controller.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserGetRequestDto {
    @ApiModelProperty(value = "유저 아이디",example = "1",required = false)
    private final Integer id;
    @ApiModelProperty(value = "유저 이메일",example = "test_email@gmail.com",required = false)
    private final String email;
    @ApiModelProperty(value = "유저 닉네임",example = "nickname",required = false)
    private final String nickname;
}
