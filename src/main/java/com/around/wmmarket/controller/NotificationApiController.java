package com.around.wmmarket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
public class NotificationApiController {
    private static final Map<String,SseEmitter> EMITTER_MAP=new HashMap<>();
    @GetMapping("/subs")
    public SseEmitter subscribe(String id){
        log.info("subs id:{}",id);
        SseEmitter emitter=new SseEmitter(10L * 60 * 1000);
        EMITTER_MAP.put(id,emitter);

        emitter.onTimeout(()->EMITTER_MAP.remove(id));
        emitter.onCompletion(()->EMITTER_MAP.remove(id));
        return emitter;
    }
    @GetMapping("/pubs")
    public void publish(String message){
        log.info("pubs message:{}",message);
        Set<String> deads=new HashSet<>();

        EMITTER_MAP.forEach((id,emitter)->{
            try{
                log.info("id:{}, message:{}",id,message);
                emitter.send(message, MediaType.APPLICATION_JSON);
            } catch (Exception e){
                deads.add(id);
                log.warn("disconnected id : {}",id);
            }
        });
        deads.forEach(EMITTER_MAP::remove);
    }
}
