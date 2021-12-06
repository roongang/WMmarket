package com.around.wmmarket.controller.dto.DealPost;

import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealState;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class DealPostGetResponseDto {
    private String userEmail;
    private Category category;
    private String title;
    private Integer price;
    private String content;
    private DealState dealState;
}
