package com.around.wmmarket.controller.dto;

import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealState;
import lombok.Getter;

@Getter
public class DealPostResponseDto {
    private Integer user;
    private Category category;
    private String title;
    private Integer price;
    private String content;
    private DealState dealState;

    public DealPostResponseDto(DealPost entity){
        // Entity의 필드 중 일부만 사용하니까 생성자로 entity를 받아 필드에 값을 넣음
        // 굳이 모든 필드를 가진 생성자가 필요하진 않으므로 Dto는 entity를 받아서 처리
        this.user = entity.getUser();
        this.category = entity.getCategory();
        this.title = entity.getTitle();
        this.price = entity.getPrice();
        this.content = entity.getContent();
        this.dealState = entity.getDealState();
    }
}