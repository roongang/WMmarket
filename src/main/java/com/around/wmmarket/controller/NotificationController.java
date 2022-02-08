package com.around.wmmarket.controller;

import com.around.wmmarket.controller.dto.notification.Message;
import com.around.wmmarket.controller.dto.notification.OutputMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Controller
public class NotificationController {
    private final SimpMessagingTemplate template;

    @MessageMapping("/chatting")
    @SendTo("/topic/messages")
    public OutputMessage send(Message message){
        log.info("from : {}, message : {}",message.getFrom(),message.getText());
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time);
    }
}
