package com.around.wmmarket.controller.dto.dealReview;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@AllArgsConstructor
@Builder
@Getter
public class DealReviewUpdateRequestDto {
    @ApiModelProperty(value = "리뷰 내용",example = "수정한 리뷰 내용입니다.",required = true)
    @NotNull
    private String content;
    @ApiModelProperty(hidden = true)
    @Null
    private String tmp;
}
