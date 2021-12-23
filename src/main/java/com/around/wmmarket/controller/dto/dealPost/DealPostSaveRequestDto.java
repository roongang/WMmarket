package com.around.wmmarket.controller.dto.dealPost;

import com.around.wmmarket.domain.deal_post.Category;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class DealPostSaveRequestDto {
    private Category category;
    private String title;
    private Integer price;
    private String content;
    private List<MultipartFile> files;
}