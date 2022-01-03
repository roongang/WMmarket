package com.around.wmmarket.controller.dto.dealPost;

import com.around.wmmarket.domain.deal_post.Category;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class DealPostSaveRequestDto {
    @ApiParam(value = "거래 글 카테고리",example = "A",required = true)
    private Category category;
    @ApiParam(value = "거래 글 제목",example = "거래 글 제목",required = true)
    private String title;
    @ApiParam(value = "거래 가격",example = "1000",required = true)
    private Integer price;
    @ApiParam(value = "거래 글 내용",example = "거래 글 내용",required = true)
    private String content;
    @ApiParam(value = "거래 글 사진들",allowMultiple = true,required = false)
    private List<MultipartFile> files;
}
