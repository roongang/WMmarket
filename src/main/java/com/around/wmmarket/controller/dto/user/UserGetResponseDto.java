package com.around.wmmarket.controller.dto.user;

import com.around.wmmarket.domain.user.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class UserGetResponseDto {
    @ApiModelProperty(value = "유저 아이디",example = "1",required = true)
    private final Integer id;
    @ApiModelProperty(value = "유저 이메일",example = "test_email@gmail.com",required = true)
    private final String email;
    @ApiModelProperty(value = "유저 닉네임",example = "nickname",required = true)
    private final String nickname;
    @ApiModelProperty(value = "유저 역할",example = "USER",required = true)
    private final Role role;
    @ApiModelProperty(value = "유저 사는곳1 시",example = "서울특별시",required = true)
    private final String city_1;
    @ApiModelProperty(value = "유저 사는곳1 구",example = "동대문구",required = true)
    private final String town_1;
    @ApiModelProperty(value = "유저 사는곳2 시",example = "인천광역시",required = true)
    private final String city_2;
    @ApiModelProperty(value = "유저 사는곳2 구",example = "연수구",required = true)
    private final String town_2;
    @ApiModelProperty(value = "유저 인증 여부", example = "0", required = true)
    private final Integer isAuth;
    @ApiModelProperty(value = "유저 글 생성시간",example = "2022-01-04 09:38:32.470811",required = true)
    private LocalDateTime createdDate;
    @ApiModelProperty(value = "유저 글 수정시간",example = "2022-01-04 09:38:32.470811",required = true)
    private LocalDateTime modifiedDate;
}
