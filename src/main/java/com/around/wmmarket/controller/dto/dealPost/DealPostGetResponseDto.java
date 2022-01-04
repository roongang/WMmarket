package com.around.wmmarket.controller.dto.dealPost;

import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealState;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class DealPostGetResponseDto {
    @ApiModelProperty(value = "유저 이메일",example = "test_email@gmail.com",required = true)
    private String userEmail;
    @ApiModelProperty(value = "거래 글 카테고리",example = "A",required = true)
    private Category category;
    @ApiModelProperty(value = "거래 글 제목",example = "거래 글 제목입니다.",required = true)
    private String title;
    @ApiModelProperty(value = "거래 글 가격",example = "1000",required = true)
    private Integer price;
    @ApiModelProperty(value = "거래 글 내용",example = "거래 글 내용입니다.",required = true)
    private String content;
    @ApiModelProperty(value = "거래 상태",example = "ONGOING",required = true)
    private DealState dealState;
}
