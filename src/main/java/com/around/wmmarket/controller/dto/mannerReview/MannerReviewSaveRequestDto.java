package com.around.wmmarket.controller.dto.mannerReview;

import com.around.wmmarket.domain.manner_review.Manner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MannerReviewSaveRequestDto {
    private final Integer buyerId;
    private final Integer sellerId;
    private final Manner manner;
}
