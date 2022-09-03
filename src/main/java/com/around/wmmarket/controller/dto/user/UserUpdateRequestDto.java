package com.around.wmmarket.controller.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {
    @ApiModelProperty(value = "유저 비밀번호",example = "update_password",required = false)
    private final String password;
    @ApiModelProperty(value = "유저 닉네임",example = "update_nickname",required = false)
    private final String nickname;
    @ApiModelProperty(value = "유저 권한",example = "ADMIN",required = false)
    //@Enum(enumClass = Role.class,isNullable = true)
    private final List<String> roles;
    @ApiModelProperty(value = "유저 사는곳1 시",example = "서울시",required = false)
    private final String city_1;
    @ApiModelProperty(value = "유저 사는곳1 구",example = "서대문구",required = false)
    private final String town_1;
    @ApiModelProperty(value = "유저 사는곳2 시",example = "인천광역시",required = false)
    private final String city_2;
    @ApiModelProperty(value = "유저 사는곳2 구",example = "서구",required = false)
    private final String town_2;
}
