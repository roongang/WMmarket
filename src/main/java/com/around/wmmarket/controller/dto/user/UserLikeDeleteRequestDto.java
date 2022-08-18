package com.around.wmmarket.controller.dto.user;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
public class UserLikeDeleteRequestDto {
    @ApiParam(value = "거래글 아이디",example = "1",required = true)
    @Min(0)
    private Integer dealPostId;
}
