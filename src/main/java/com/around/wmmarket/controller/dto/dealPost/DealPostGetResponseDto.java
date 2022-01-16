package com.around.wmmarket.controller.dto.dealPost;

import com.around.wmmarket.domain.deal_post.Category;
import com.around.wmmarket.domain.deal_post.DealState;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
public class DealPostGetResponseDto {
    @ApiModelProperty(value = "거래 글 아이디",example = "1",required = true)
    private Integer id;
    @ApiModelProperty(value = "유저 아이디",example = "1",required = true)
    private Integer userId;
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
    @ApiModelProperty(value = "거래 글 생성시간",example = "2022-01-04 09:38:32.470811",required = true)
    private LocalDateTime createdDate;
    @ApiModelProperty(value = "거래 글 수정시간",example = "2022-01-04 09:38:32.470811",required = true)
    private LocalDateTime modifiedDate;
    @ApiModelProperty(value = "거래 글 이미지 아이디 리스트",example = "[1,2,3]",required = true)
    private List<Integer> imageIds;

    @Builder
    public DealPostGetResponseDto(
            Integer id,
            Integer userId,
            Category category,
            String title,
            Integer price,
            String content,
            DealState dealState,
            LocalDateTime createdDate,
            LocalDateTime modifiedDate,
            List<Integer> imageIds
    ){
        this.id=id;
        this.userId=userId;
        this.category=category;
        this.title=title;
        this.price=price;
        this.content=content;
        this.dealState=dealState;
        this.createdDate=createdDate;
        this.modifiedDate=modifiedDate;
        this.imageIds=imageIds!=null?imageIds:Arrays.asList();
    }

    public DealPostGetResponseDto(
            Integer id,
            Integer userId,
            Category category,
            String title,
            Integer price,
            String content,
            DealState dealState,
            LocalDateTime createdDate,
            LocalDateTime modifiedDate
    ){
        this.id=id;
        this.userId=userId;
        this.category=category;
        this.title=title;
        this.price=price;
        this.content=content;
        this.dealState=dealState;
        this.createdDate=createdDate;
        this.modifiedDate=modifiedDate;
    }
}
