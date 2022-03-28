package com.around.wmmarket.controller.dto.dealPostImage;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class DealPostImageSaveResponseDto {
    @ApiModelProperty(value = "거래 글 이미지 아이디 리스트",example = "[1,2,3]",required = true)
    private final List<Integer> ids;
    @ApiModelProperty(value = "거래 글 이미지 이름 리스트",example = "[1.jpg,2.jpg,3.jpg]",required = true)
    private final List<String> names;
}
