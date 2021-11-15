package com.around.wmmarket.controller.dto;

import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealPost;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DealPostSaveRequestDto {
    private Integer user;
    private Category category;
    private String title;
    private Integer price;
    private String content;
    private DealState dealState;
    //이미지 입력??
    //거래 성공 입력??

    @Builder
    public DealPostSaveRequestDto(Integer user,String category,String title,Integer price,String content,String dealState){
        this.user=user;
        this.category=Category.valueOf(category);
        this.title=title;
        this.price=price;
        this.content=content;
        this.dealState=DealState.valueOf(dealState);
    }

    public DealPost toEntity(){
        return DealPost.builder()
                .user(user)
                .category(category)
                .title(title)
                .price(price)
                .content(content)
                .dealState(dealState)
                .build();
    }
}
