package com.around.wmmarket.common;

import com.around.wmmarket.common.error.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> toResponse(SuccessResponse response){
        Map<String,Object> map=new HashMap<>();
        map.put("timestamp",LocalDateTime.now());
        map.put("message",response.getMessage()==null?"":response.getMessage());
        map.put("status",response.getHttpStatus().value());
        map.put("data",response.getData()==null?new ArrayList<>():response.getData());

        return ResponseEntity
                .status(response.getHttpStatus())
                .headers(response.getHttpHeaders())
                .body(map);
    }

    public static ResponseEntity<Object> toResponse(ErrorResponse errorResponse){
        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

    public static ResponseEntity<Object> toResponse(ResourceResponse resourceResponse){
        return ResponseEntity
                .status(resourceResponse.getHttpStatus())
                .headers(resourceResponse.getHttpHeaders())
                .body(resourceResponse.getResource());
    }
}
