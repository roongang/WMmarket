package com.around.wmmarket.controller.dto.dealPost;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DealPostSaveResponseDto {
    @ApiModelProperty(value = "거래 글 아이디",example = "1",required = true)
    private final Integer id;
}
