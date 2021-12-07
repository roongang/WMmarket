package com.around.wmmarket.controller.dto.DealPost;

import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealState;
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
    private DealState dealState;
    private List<MultipartFile> files; // TODO : files 로 이름 바꾸자
}
