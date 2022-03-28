package com.around.wmmarket.domain.emitter;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepository {
    public final Map<String, SseEmitter> emitters=new ConcurrentHashMap<>();
    private final Map<String,Object> eventCache=new ConcurrentHashMap<>();

    public SseEmitter save(String id,SseEmitter emitter){
        emitters.put(id,emitter);
        return emitter;
    }
    public Object saveEventCache(String id,Object event){
        eventCache.put(id,event);
        return event;
    }
    public Map<String,SseEmitter> findAllStartWithById(String id){
        return emitters.entrySet().stream()
                .filter(entry->entry.getKey().startsWith(id))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    public Map<String,Object> findAllEventCacheStartWithById(String id){
        return eventCache.entrySet().stream()
                .filter(entry->entry.getKey().startsWith(id))
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
    }
    public void deleteAllStartWithId(String id){
        emitters.forEach((key,emitter)->{
            if(key.startsWith(id)){
                emitters.remove(key);
            }
        });
    }
    public void deleteById(String id){
        emitters.remove(id);
    }
    public void deleteAllEventCacheStartWithId(String id){
        eventCache.forEach((key,value)->{
            if(key.startsWith(id)){
                eventCache.remove(key);
            }
        });
    }
}
