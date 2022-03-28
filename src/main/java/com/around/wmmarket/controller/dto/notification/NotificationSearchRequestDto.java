package com.around.wmmarket.controller.dto.notification;

import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class NotificationSearchRequestDto {
    @ApiParam(value = "알림 수신자 아이디",example = "1",required = false)
    private String userId;
    @ApiParam(value = "알림 내용",example = "알림입니다.",required = false)
    private String content;
    @ApiParam(value = "알림 타입",example = "ACTIVITY",required = false)
    private String type;
    @ApiParam(value = "알림 조회 여부",example = "true",required = false)
    private String isRead;
    @ApiParam(value = "알림 생성시간",example = "2022-01-04 09:38:32.470811",required = false)
    private String createdDate;
}
