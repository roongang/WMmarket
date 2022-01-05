package com.around.wmmarket.controller.dto.dealPost;

import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealState;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DealPostUpdateRequestDto {
    @ApiModelProperty(value = "거래 글 카테고리",example = "B",required = false)
    private Category category;
    @ApiModelProperty(value = "거래 글 제목",example = "수정한 거래 글 제목",required = false)
    private String title;
    @ApiModelProperty(value = "거래 가격",example = "2000",required = false)
    private Integer price;
    @ApiModelProperty(value = "거래 글 내용",example = "수정한 거래 글 내용",required = false)
    private String content;
    @ApiModelProperty(value = "구매자 아이디",example = "2",required = false)
    private Integer buyerId;
    @ApiModelProperty(value = "거래 글 상태",example = "DONE",required = false)
    private DealState dealState;
}
