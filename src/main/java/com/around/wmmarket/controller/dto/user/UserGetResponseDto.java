package com.around.wmmarket.controller.dto.user;

import com.around.wmmarket.domain.user.Role;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.*;

@AllArgsConstructor
@Getter
@Builder
public class UserGetResponseDto {
    @ApiModelProperty(value = "유저 이메일",example = "test_email@gmail.com",required = true)
    private String email;
    @ApiModelProperty(value = "유저 닉네임",example = "nickname",required = true)
    private String nickname;
    @ApiModelProperty(value = "유저 역할",example = "USER",required = true)
    private Role role;
    @ApiModelProperty(value = "유저 사는곳1 시",example = "서울특별시",required = true)
    private String city_1;
    @ApiModelProperty(value = "유저 사는곳1 구",example = "동대문구",required = true)
    private String town_1;
    @ApiModelProperty(value = "유저 사는곳2 시",example = "인천광역시",required = true)
    private String city_2;
    @ApiModelProperty(value = "유저 사는곳2 구",example = "연수구",required = true)
    private String town_2;
    @ApiModelProperty(value = "유저 인증 여부", example = "0", required = true)
    private Integer isAuth;
    @ApiModelProperty(value = "유저 인증 코드",example = "1234", required = true)
    private String code;
}
