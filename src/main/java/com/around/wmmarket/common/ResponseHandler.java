package com.around.wmmarket.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj){
        Map<String,Object> map=new HashMap<>();
        map.put("message",message);
        map.put("status",status);
        map.put("data",responseObj);

        return ResponseEntity
                .status(status)
                .body(map);
    }
}
