package com.around.wmmarket.controller.dto.user;

import com.around.wmmarket.common.validation.Enum;
import com.around.wmmarket.domain.user.Role;
import io.swagger.annotations.ApiParam;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class UserSaveRequestDto {
    @ApiParam(value = "유저 이메일",example = "test_email@gmail.com",required = true)
    @Email
    private String email;
    @ApiParam(value = "유저 비밀번호",example = "test_password",required = true)
    @NotEmpty
    private String password;
    @ApiParam(value = "유저 이미지",allowMultiple = true,required = false)
    private MultipartFile image;
    @ApiParam(value = "유저 닉네임",example = "nickname",required = true)
    @NotBlank
    private String nickname;
    @ApiParam(value = "유저 역할",example = "USER",required = true)
    @Enum(enumClass = Role.class)
    private String role;
    @ApiParam(value = "유저 사는곳1 시",example = "서울특별시",required = false)
    private String city_1;
    @ApiParam(value = "유저 사는곳1 구",example = "동대문구",required = false)
    private String town_1;
    @ApiParam(value = "유저 사는곳2 시",example = "인천광역시",required = false)
    private String city_2;
    @ApiParam(value = "유저 사는곳2 구",example = "연수구",required = false)
    private String town_2;
}
