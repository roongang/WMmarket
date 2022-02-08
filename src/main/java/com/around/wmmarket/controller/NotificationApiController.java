package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.notification.Message;
import com.around.wmmarket.controller.dto.notification.NotificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class NotificationApiController {
    private final SimpMessagingTemplate template;

    @MessageMapping("/message")
    public void sendMessage(@Payload NotificationRequestDto requestDto){
        log.info("{} send message to {} : {}",requestDto.getFrom(),requestDto.getTo(),requestDto.getMessage());
        template.convertAndSend(requestDto.getTo(),requestDto.getMessage());
    }

    @MessageMapping("/chat")
    public void send(Message message){
        log.info("from : {}, message : {}",message.getFrom(),message.getMessage());

    }
}
