package com.around.wmmarket.controller.dto.keyword;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class KeyWordGetResponseDto {
    @ApiModelProperty(value = "키워드 아이디",example = "1",required = true)
    private Integer id;
    @ApiModelProperty(value = "유저 아이디",example = "1",required = true)
    private Integer userId;
    @ApiModelProperty(value = "유저 닉네임",example = "userNickname",required = true)
    private String userNickname;
    @ApiModelProperty(value = "키워드 낱말",example = "보드게임",required = true)
    private String word;
    @ApiModelProperty(value = "키워드 생성시간",example = "2022-01-04 09:38:32.470811",required = true)
    private LocalDateTime createdDate;
}
