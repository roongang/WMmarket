package com.around.wmmarket.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ResourceResponse {
    private final LocalDateTime timestamp;
    private final HttpHeaders httpHeaders;
    private final HttpStatus httpStatus;
    private final String message;
    private final Resource resource;

    @Builder
    public ResourceResponse(HttpHeaders httpHeaders,HttpStatus httpStatus,String message,Resource resource){
        this.timestamp= LocalDateTime.now();
        this.httpHeaders=httpHeaders;
        this.httpStatus=httpStatus;
        this.message=message;
        this.resource=resource;
    }
}
