package com.around.wmmarket.controller.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserSaveResponseDto {
    @ApiModelProperty(value = "유저 아이디",example = "1",required = true)
    private final Integer id;
}
