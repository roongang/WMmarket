package com.around.wmmarket.common.error;

import com.around.wmmarket.common.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<Object> handlerCustomException(CustomException e){
        log.error("CustomException : {}",e.getErrorCode());
        return ResponseHandler.toResponse(ErrorResponse.of(e.getErrorCode()));
    }

    // other exception
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> otherException(Exception e){
        log.error("other exception : {}",e.toString());
        return ResponseHandler.toResponse(ErrorResponse.of(ErrorCode.UNDEFINED_ERROR,e.toString()));
    }
}
