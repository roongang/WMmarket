package com.around.wmmarket.controller.dto.mannerReview;

import com.around.wmmarket.domain.manner_review.Manner;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MannerReviewGetResponseDto {
    private final Integer sellerId;
    private final Integer buyerId;
    private final Manner manner;
}
