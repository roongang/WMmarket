package com.around.wmmarket.controller;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.domain.notification.NotificationType;
import com.around.wmmarket.domain.user.Role;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.domain.user.User;
import com.around.wmmarket.domain.user.UserRepository;
import com.around.wmmarket.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequestMapping(Constants.API_PATH)
@RequiredArgsConstructor
@RestController
public class NotificationApiController {
    private final NotificationService notificationService;
    // TEST CODE
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/subs",produces = "text/event-stream")
    public SseEmitter subscribe(@RequestParam Integer userId,
                                @RequestParam(value = "lastEventId", required = false, defaultValue = "") String lastEventId){
        // TEST CODE make user
        if(userRepository.findById(userId).isEmpty()){
            userRepository.save(User.builder()
                    .email("user"+userId+"@email")
                    .password(passwordEncoder.encode("password"))
                    .nickname("nickname"+userId)
                    .role(Role.USER)
                    .build());
        }
        log.info("subs userId:{},lastEventId:{}",userId,lastEventId);
        return notificationService.subscribe(userId,lastEventId);
    }
    // TEST CODE
    @GetMapping("/pubs")
    public void sendToAllUser(String message){
        log.info("pubs message:{}",message);
        userRepository.findAll()
                        .forEach(user->notificationService.send(user, NotificationType.ACTIVITY,message,"sse"));
    }
    @GetMapping("/notifications")
    public Object getNotifications(@AuthenticationPrincipal SignedUser signedUser){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .data(notificationService.findAll(signedUser))
                .message("알림 반환 성공했습니다.")
                .build());
    }
    @PutMapping("/notifications/{notificationId}/is-read")
    public Object readNotification(@AuthenticationPrincipal SignedUser signedUser,
                                   @PathVariable("notificationId") Integer notificationId){
        notificationService.readNotification(signedUser,notificationId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("알림 읽음으로 수정 성공했습니다.")
                .build());
    }
}
