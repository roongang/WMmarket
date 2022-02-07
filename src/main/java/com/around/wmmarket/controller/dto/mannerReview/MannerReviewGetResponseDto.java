package com.around.wmmarket.controller.dto.mannerReview;

import com.around.wmmarket.domain.manner_review.Manner;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Getter
@Builder
public class MannerReviewGetResponseDto {
    @ApiModelProperty(value = "판매자 아이디",example = "1",required = true)
    @Min(1)
    private final Integer sellerId;
    @ApiModelProperty(value = "구매자 아이디",example = "2",required = true)
    @Min(1)
    private final Integer buyerId;
    @ApiModelProperty(value = "매너 타입",example = "GOOD_KIND",required = true)
    private final Manner manner;
    @ApiModelProperty(value = "매너 리뷰 생성시간",example = "2022-01-04 09:38:32.470811",required = true)
    private LocalDateTime createdDate;
    @ApiModelProperty(value = "매너 리뷰 수정시간",example = "2022-01-04 09:38:32.470811",required = true)
    private LocalDateTime modifiedDate;
}
