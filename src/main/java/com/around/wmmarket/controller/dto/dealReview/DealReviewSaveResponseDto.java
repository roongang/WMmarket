package com.around.wmmarket.controller.dto.dealReview;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DealReviewSaveResponseDto {
    @ApiModelProperty(value = "거래 리뷰 아이디",example = "1",required = true)
    private final Integer id;
}
