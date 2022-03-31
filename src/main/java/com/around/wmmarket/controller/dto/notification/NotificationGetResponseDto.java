package com.around.wmmarket.controller.dto.notification;

import com.around.wmmarket.domain.notification.NotificationType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class NotificationGetResponseDto {
    @ApiModelProperty(value = "알림 아이디",example = "1",required = true)
    private Integer id;
    @ApiModelProperty(value = "알림 내용",example = "수박마켓 알림입니다.",required = true)
    private String content;
    @ApiModelProperty(value = "알림 리소스 url",example = "/api/v1/deal-posts/1",required = true)
    private String url;
    @ApiModelProperty(value = "알림 타입",example = "ACTIVITY",required = true)
    private NotificationType type;
    @ApiModelProperty(value = "알림 읽음 여부",example = "false",required = true)
    private Boolean isRead;
    @ApiModelProperty(value = "알림 생성시간",example = "2022-01-04 09:38:32.470811",required = true)
    private LocalDateTime createdDate;
}
