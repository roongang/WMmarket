package com.around.wmmarket.controller.dto;

import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.user.User;
import lombok.Getter;

@Getter
public class DealPostListResponseDto {
    //private User user;
    private Category category;
    private String title;
    private Integer price;
    private String content;
    private DealState dealState;

    public DealPostListResponseDto(DealPost entity){
       // this.user = entity.getUser();
        this.category = entity.getCategory();
        this.title = entity.getTitle();
        this.price = entity.getPrice();
        this.content = entity.getContent();
        this.dealState = entity.getDealState();
    }
}
