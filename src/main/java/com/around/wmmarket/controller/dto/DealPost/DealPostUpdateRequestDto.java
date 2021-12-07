package com.around.wmmarket.controller.dto.DealPost;

import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DealPostUpdateRequestDto {
    private Integer dealPostId;
    private Category category;
    private String title;
    private Integer price;
    private String content;
    private DealState dealState;
}
