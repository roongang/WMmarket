package com.around.wmmarket.controller.dto.DealReview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class DealReviewUpdateRequestDto {
    private Integer dealReviewId;
    private String content;
}
