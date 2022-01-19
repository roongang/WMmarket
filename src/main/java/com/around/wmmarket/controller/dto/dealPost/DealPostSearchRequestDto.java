package com.around.wmmarket.controller.dto.dealPost;

import com.around.wmmarket.controller.dto.PagingRequestDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DealPostSearchRequestDto extends PagingRequestDto {
    private String id;
    private String userId;
    private String category;
    private String title;
    private String price;
    private String content;
    private String dealState;
    private String createdDate;
    private String modifiedDate;
}
