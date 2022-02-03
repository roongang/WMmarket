package com.around.wmmarket.controller.dto.mannerReview;

import com.around.wmmarket.common.validation.Enum;
import com.around.wmmarket.domain.manner_review.Manner;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;

@Getter
@AllArgsConstructor
@Builder
public class MannerReviewSaveRequestDto {
    @ApiModelProperty(value = "구매자 아이디",example = "2",required = true)
    @Min(1)
    private final Integer buyerId;
    @ApiModelProperty(value = "판매자 아이디",example = "1",required = true)
    @Min(1)
    private final Integer sellerId;
    @ApiModelProperty(value = "매너 타입",example = "GOOD_KIND",required = true)
    @Enum(enumClass = Manner.class)
    private final String manner;
}