package com.around.wmmarket.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class PagingRequestDto {
    private String size="10";
    private String page="0";
    private String sort;
}
