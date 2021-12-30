package com.around.wmmarket.common;

import com.around.wmmarket.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
public class SuccessResponse {
    private final LocalDateTime timestamp;
    private final Integer status;
    private final String message;
    private final Object data;

    @Builder
    public SuccessResponse(HttpStatus status,String message,Object data){
        this.timestamp=LocalDateTime.now();
        this.status=status.value();
        this.message=message==null?"":message;
        this.data=data==null?new ArrayList<>():data;
    }
}
