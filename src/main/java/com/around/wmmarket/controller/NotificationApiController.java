package com.around.wmmarket.controller;

import com.around.wmmarket.domain.notification.NotificationType;
import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class NotificationApiController {
    private static final Map<String,SseEmitter> EMITTER_MAP=new HashMap<>();

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping(value = "/subs",produces = "text/event-stream")
    public SseEmitter subscribe(@RequestParam Integer userId,
                                @RequestParam(value = "lastEventId", required = false, defaultValue = "") String lastEventId){
        // make user
        userRepository.save(User.builder()
                .email("user"+userId+"@email")
                .password("password")
                .nickname("nickname"+userId)
                .role(Role.USER)
                .build());
        log.info("subs userId:{},lastEventId:{}", userId,lastEventId);
        return notificationService.subscribe(userId,lastEventId);
    }
    @GetMapping("/pubs")
    public void publish(String message){
        log.info("pubs message:{}",message);
        userRepository.findAll().stream()
                        .forEach(user->notificationService.send(user, NotificationType.ACTIVITY,message,"sse"));
    }

}
