package com.around.wmmarket.controller.dto.dealReview;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class DealReviewGetResponseDto {
    @ApiModelProperty(value = "거래 리뷰 아이디",example = "1",required = true)
    private Integer id;
    @ApiModelProperty(value = "판매자 아이디",example = "1",required = true)
    private Integer sellerId;
    @ApiModelProperty(value = "구매자 아이디",example = "2",required = true)
    private Integer buyerId;
    @ApiModelProperty(value = "거래 리뷰 내용",example = "거래 리뷰 내용입니다.",required = true)
    private String content;
    @ApiModelProperty(value = "거래 리뷰 생성시간",example = "2022-01-04 09:38:32.470811",required = true)
    private LocalDateTime createdDate;
    @ApiModelProperty(value = "거래 리뷰 수정시간",example = "2022-01-04 09:38:32.470811",required = true)
    private LocalDateTime modifiedDate;
    @ApiModelProperty(value = "거래 글 아이디",example = "1",required = true)
    private Integer dealPostId;
}
