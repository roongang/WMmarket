package com.around.wmmarket.controller.dto.dealReview;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class DealReviewUpdateRequestDto {
    @ApiModelProperty(value = "거래 리뷰 아이디",example = "1",required = true)
    private Integer dealReviewId;
    @ApiModelProperty(value = "리뷰 내용",example = "수정한 리뷰 내용입니다.",required = false)
    private String content;
}
