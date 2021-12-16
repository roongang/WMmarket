package com.around.wmmarket.controller.dto.dealReview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DealReviewSaveRequestDto {
    private final Integer dealPostId;
    private final String content;
}
