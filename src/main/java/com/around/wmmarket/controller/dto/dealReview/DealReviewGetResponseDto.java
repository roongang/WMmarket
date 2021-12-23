package com.around.wmmarket.controller.dto.dealReview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class DealReviewGetResponseDto {
    private Integer sellerId;
    private Integer buyerId;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Integer dealPostId;
}
