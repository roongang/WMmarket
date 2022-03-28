package com.around.wmmarket.controller;

import com.around.wmmarket.common.Constants;
import com.around.wmmarket.common.ResponseHandler;
import com.around.wmmarket.common.SuccessResponse;
import com.around.wmmarket.controller.dto.notification.NotificationGetResponseDto;
import com.around.wmmarket.controller.dto.notification.NotificationSearchRequestDto;
import com.around.wmmarket.domain.user.SignedUser;
import com.around.wmmarket.service.notification.NotificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Min;

@Slf4j
@Validated
@RequestMapping(Constants.API_PATH)
@RequiredArgsConstructor
@RestController
public class NotificationApiController {
    private final NotificationService notificationService;

    @ApiOperation(value = "Emitter 구독하기")
    @GetMapping(value = "/subs",produces = "text/event-stream")
    public SseEmitter subscribe(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                @RequestParam(value = "lastEventId", required = false, defaultValue = "") String lastEventId){
        return notificationService.subscribe(signedUser,lastEventId);
    }
    @ApiOperation(value = "로그인한 유저의 모든 알림 받기")
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : List<NotificationGetResponseDto>",response = NotificationGetResponseDto.class),
    })
    @GetMapping("/notifications")
    public Object getNotifications(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .data(notificationService.findAll(signedUser))
                .message("알림 반환 성공했습니다.")
                .build());
    }
    @ApiOperation(value = "알림 읽은 상태로 변경하기")
    @PutMapping("/notifications/{notificationId}/is-read")
    public Object readNotification(@ApiIgnore @AuthenticationPrincipal SignedUser signedUser,
                                   @Min(1) @PathVariable("notificationId") Integer notificationId){
        notificationService.readNotification(signedUser,notificationId);
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .message("알림 읽음으로 수정 성공했습니다.")
                .build());
    }
    @ApiOperation(value = "알림 검색하기")
    @ApiResponses({
            @ApiResponse(code = 200,message = "return data : slice",response = Slice.class),
    })
    @GetMapping("/notifications/page")
    public Object searchNotifications(NotificationSearchRequestDto requestDto){
        return ResponseHandler.toResponse(SuccessResponse.builder()
                .status(HttpStatus.OK)
                .data(notificationService.findByFilter(requestDto))
                .message("알림 검색 성공했습니다.")
                .build());
    }
}