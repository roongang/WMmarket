package com.around.wmmarket.controller.dto.keyword;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class KeyWordSaveResponseDto {
    @ApiModelProperty(value = "카테고리 아이디",example = "1",required = true)
    private final Integer id;
}
