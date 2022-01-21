package com.around.wmmarket.controller.dto.user;

import com.around.wmmarket.controller.dto.PagingRequestDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequestDto extends PagingRequestDto {
    @ApiModelProperty(value = "유저 이메일",example = "test_email@gmail.com",required = false)
    private String email;
    @ApiModelProperty(value = "유저 닉네임",example = "nickname",required = false)
    private String nickname;
    @ApiModelProperty(value = "유저 역할",example = "USER",required = false)
    private String role;
    @ApiModelProperty(value = "유저 사는곳1 시",example = "서울특별시",required = false)
    private String city_1;
    @ApiModelProperty(value = "유저 사는곳1 구",example = "동대문구",required = false)
    private String town_1;
    @ApiModelProperty(value = "유저 사는곳2 시",example = "인천광역시",required = false)
    private String city_2;
    @ApiModelProperty(value = "유저 사는곳2 구",example = "연수구",required = false)
    private String town_2;
    @ApiModelProperty(value = "유저 인증 여부", example = "0", required = false)
    private String isAuth;
    @ApiModelProperty(value = "유저 글 생성시간",example = "2022-01-04 09:38:32.470811",required = false)
    private String createdDate;
    @ApiModelProperty(value = "유저 글 수정시간",example = "2022-01-04 09:38:32.470811",required = false)
    private String modifiedDate;
}