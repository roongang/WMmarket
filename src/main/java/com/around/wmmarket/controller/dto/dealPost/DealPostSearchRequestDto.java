package com.around.wmmarket.controller.dto.dealPost;

import com.around.wmmarket.controller.dto.PagingRequestDto;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DealPostSearchRequestDto extends PagingRequestDto {
    @ApiParam(value = "유저 아이디",example = "1",required = false)
    private String userId;
    @ApiParam(value = "거래 글 카테고리",example = "A",required = false)
    private String category;
    @ApiParam(value = "거래 글 제목",example = "dealPost_title",required = false)
    private String title;
    @ApiParam(value = "거래 글 가격",example = "goe:1000,lt:2000",required = false)
    private String price;
    @ApiParam(value = "거래 글 내용",example = "ct:content",required = false)
    private String content;
    @ApiParam(value = "거래 글 상태",example = "ONGOING",required = false)
    private String dealState;
    @ApiParam(value = "거래 글 생성시간",example = "2022-01-04 09:38:32.470811",required = false)
    private String createdDate;
    @ApiParam(value = "거래 글 수정시간",example = "2022-01-04 09:38:32.470811",required = false)
    private String modifiedDate;
}
