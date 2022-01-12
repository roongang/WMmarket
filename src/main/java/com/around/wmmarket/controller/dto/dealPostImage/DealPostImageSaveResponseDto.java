package com.around.wmmarket.controller.dto.dealPostImage;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class DealPostImageSaveResponseDto {
    @ApiModelProperty(value = "거래 글 이미지 아이디 리스트",example = "[1,2,3]",required = true)
    private final List<Integer> ids;
}
