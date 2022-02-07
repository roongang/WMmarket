package com.around.wmmarket.controller.dto.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NotificationRequestDto {
    private final String from;
    private final String to;
    private final String message;
}
