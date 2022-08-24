package com.around.wmmarket.controller.dto.userRole;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class UserRoleSaveResponseDto {
    @ApiModelProperty(value = "유저 권한 아이디 리스트",example = "[1,2,3]",required = true)
    private final List<Integer> ids;
    @ApiModelProperty(value = "유저 권한 리스트",example = "[USER,ADMIN]",required = true)
    private final List<String> roles;
}
