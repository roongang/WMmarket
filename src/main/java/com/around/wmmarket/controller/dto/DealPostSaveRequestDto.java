package com.around.wmmarket.controller.dto;

import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DealPostSaveRequestDto {
    private Category category;
    private String title;
    private Integer price;
    private String content;
    private Character dealState;

    @Builder
    public DealPostSaveRequestDto(Category category,String title,Integer price,String content,Character dealState){
        this.category=category;
        this.title=title;
        this.price=price;
        this.content=content;
        this.dealState=dealState;
    }
}
