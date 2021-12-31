package com.around.wmmarket.controller.dto.dealReview;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class DealReviewUpdateRequestDto {
    @ApiParam(value = "거래 글 번호",example = "1",required = true)
    private Integer dealReviewId;
    @ApiParam(value = "거래 리뷰 내용",example = "거래 리뷰입니다.",required = true)
    private String content;
}
