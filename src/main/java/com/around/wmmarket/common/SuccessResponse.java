package com.around.wmmarket.common;

import com.around.wmmarket.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Builder
public class SuccessResponse {
    private final HttpHeaders httpHeaders;
    private final HttpStatus httpStatus;
    private final String message;
    private final Object data;


}
