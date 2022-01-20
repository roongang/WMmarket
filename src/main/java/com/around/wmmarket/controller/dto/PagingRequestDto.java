package com.around.wmmarket.controller.dto;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class PagingRequestDto {
    @ApiParam(value = "한 페이지당 리소스 수",example = "3",defaultValue = "10",required = false)
    private String size="10";
    @ApiParam(value = "페이지 인덱스",example = "0",defaultValue = "0",required = false)
    private String page="0";
    @ApiParam(value = "정렬방법",example = "price:desc, createdDate:asc",defaultValue = "id:asc",required = false)
    private String sort;
}