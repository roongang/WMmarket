package com.around.wmmarket.controller.dto.dealReview;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@AllArgsConstructor
@Builder
public class DealReviewSaveRequestDto {
    @ApiModelProperty(value = "거래 글 아이디",example = "1",required = true)
    @DecimalMin(value="1")
    private final Integer dealPostId;
    @ApiModelProperty(value = "리뷰 내용",example = "리뷰 내용입니다.",required = true)
    @NotNull
    private final String content;
}
