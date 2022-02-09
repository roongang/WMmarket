package com.around.wmmarket.controller.dto.dealPost;

import com.around.wmmarket.common.validation.Enum;
import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealState;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Builder
@AllArgsConstructor
public class DealPostUpdateRequestDto {
    @ApiModelProperty(value = "거래 글 카테고리",example = "B",required = false)
    @Enum(enumClass = Category.class,isNullable = true)
    private final String category;
    @ApiModelProperty(value = "거래 글 제목",example = "수정한 거래 글 제목",required = false)
    private final String title;
    @ApiModelProperty(value = "거래 가격",example = "2000",required = false)
    @Min(0)
    private final Integer price;
    @ApiModelProperty(value = "거래 글 내용",example = "수정한 거래 글 내용",required = false)
    private final String content;
    @ApiModelProperty(value = "구매자 아이디",example = "2",required = false)
    @Min(1)
    private final Integer buyerId;
    @ApiModelProperty(value = "거래 글 상태",example = "DONE",required = false)
    @Enum(enumClass = DealState.class,isNullable = true)
    private final String dealState;
    @ApiModelProperty(value = "거래 글 조회수 증가",example = "1",required = false)
    @Min(1) @Max(100)
    private final Integer viewCnt;
}
