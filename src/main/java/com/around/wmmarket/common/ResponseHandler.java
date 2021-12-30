package com.around.wmmarket.common;

import com.around.wmmarket.common.error.ErrorResponse;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {
    public static ResponseEntity<Object> toResponse(SuccessResponse response){
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    public static ResponseEntity<Object> toResponse(ErrorResponse errorResponse){
        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

    public static ResponseEntity<Object> toResponse(ResourceResponse resourceResponse){
        return ResponseEntity
                .status(resourceResponse.getStatus())
                .headers(resourceResponse.getHeaders())
                .body(resourceResponse.getResource());
    }
}
