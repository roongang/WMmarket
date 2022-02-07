package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.notification.NotificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
@RestController
public class NotificationApiController {
    private final SimpMessagingTemplate template;

    @MessageMapping(value = "/notification/message")
    public void sendMessage(NotificationRequestDto requestDto){
        log.info("{} send message to {} : {}",requestDto.getFrom(),requestDto.getTo(),requestDto.getMessage());
        template.convertAndSend(requestDto.getTo(),requestDto.getMessage());
    }
}
