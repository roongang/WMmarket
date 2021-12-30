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
    private final HttpHeaders headers;
    private final Integer status;
    private final String message;
    private final Resource resource;

    @Builder
    public ResourceResponse(HttpHeaders headers,HttpStatus status,String message,Resource resource){
        this.timestamp= LocalDateTime.now();
        this.headers=headers;
        this.status=status.value();
        this.message=message;
        this.resource=resource;
    }
}
