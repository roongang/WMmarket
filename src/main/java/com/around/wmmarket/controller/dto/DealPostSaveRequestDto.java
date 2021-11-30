package com.around.wmmarket.controller.dto;

import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealState;
import com.around.wmmarket.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
public class DealPostSaveRequestDto {
    private Category category;
    private String title;
    private Integer price;
    private String content;
    private DealState dealState;

    @Builder
    public DealPostSaveRequestDto(Category category, String title, Integer price, String content, DealState dealState, List<MultipartFile> multipartFiles){
        this.category=category;
        this.title=title;
        this.price=price;
        this.content=content;
        this.dealState=dealState;
    }
}
