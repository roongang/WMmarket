package com.around.wmmarket.service.notification;

import com.around.wmmarket.common.error.CustomException;
import com.around.wmmarket.common.error.ErrorCode;
import com.around.wmmarket.controller.dto.notification.NotificationGetResponseDto;
import com.around.wmmarket.domain.emitter.EmitterRepository;
import com.around.wmmarket.domain.notification.Notification;
import com.around.wmmarket.domain.notification.NotificationRepository;
import com.around.wmmarket.domain.notification.NotificationType;
import com.around.wmmarket.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {
    private static final Long TIMEOUT=60L*1000*60;
    private static final String EVENT_SSE="sse";

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    public SseEmitter subscribe(Integer userId,String lastEventId){
        String emitterId=userId+"_"+System.currentTimeMillis();
        SseEmitter emitter=emitterRepository.save(emitterId,new SseEmitter(TIMEOUT));

        emitter.onCompletion(()->emitterRepository.deleteById(emitterId));
        emitter.onTimeout(()->emitterRepository.deleteById(emitterId));

        // SEND EVENT FOR ERROR 503
        _send(emitter,emitterId,EVENT_SSE,"EventStream_Created.[userId="+userId+"]");

        // lastEventId
        if(lastEventId!=null && !lastEventId.isEmpty()){
            // 못보낸 이벤트들
            Map<String,Object> events=emitterRepository.findAllEventCacheStartWithById(Integer.toString(userId));
            events.entrySet().stream()
                    .filter(entry-> lastEventId.compareTo(entry.getKey())<0)
                    .forEach(entry->{_send(emitter,entry.getKey(),EVENT_SSE,entry.getValue());});

        }
        return emitter;
    }
    private void _send(SseEmitter emitter,String id,String eventName,Object data){
        try {
            log.info("send message id:{},eventName:{}",id,eventName);
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name(eventName)
                    .data(data));
        } catch (IOException e){
            // emitter 가 존재하지 않음
            emitterRepository.deleteById(id);
            throw new CustomException(ErrorCode.UNDEFINED_ERROR,e.getMessage());
        }
    }
    @Transactional
    public void send(User receiver, NotificationType type,String content,String eventName){
        Notification notification=Notification.builder()
                .receiver(receiver)
                .content(content)
                .type(type)
                .build();
        String userId=Integer.toString(receiver.getId());
        notificationRepository.save(notification);
        Map<String,SseEmitter> emitters=emitterRepository.findAllStartWithById(userId);
        emitters.forEach((key,emitter)-> {
            // 보낸 이벤트 저장
            emitterRepository.saveEventCache(key,notification);
            // event 를 object(dto)로 설정해서 보낼수있음.
            _send(emitter,key,eventName,content);
        });
    }
    public NotificationGetResponseDto findAllById(Integer userId){

    }
}
