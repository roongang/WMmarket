package com.around.wmmarket.controller.dto.mannerReview;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;

@Getter
@RequiredArgsConstructor
public class MannerReviewSaveResponseDto {
    @ApiModelProperty(value = "매너 리뷰 아이디",example = "1",required = true)
    @Min(1)
    private final Integer id;
}
