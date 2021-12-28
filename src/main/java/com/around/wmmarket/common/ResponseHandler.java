package com.around.wmmarket.common;

import com.around.wmmarket.common.error.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> toResponse(String message, HttpStatus status, Object responseObj){
        Map<String,Object> map=new HashMap<>();
        map.put("timestamp", LocalDateTime.now());
        map.put("message",message);
        map.put("statusCode",status.value());
        map.put("statusMessage",status.name());
        map.put("data",responseObj);

        return ResponseEntity
                .status(status)
                .body(map);
    }

    public static ResponseEntity<Object> toResponse(ErrorCode errorCode){
        Map<String,Object> map=new HashMap<>();
        map.put("timestamp",LocalDateTime.now());
        map.put("statusCode",errorCode.getStatus().value());
        map.put("statusMessage",errorCode.getStatus().name());
        map.put("message",errorCode.name());
        map.put("detail",errorCode.getDetail());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(map);
    }
}
