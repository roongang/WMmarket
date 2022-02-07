package com.around.wmmarket.common.error;

import com.around.wmmarket.common.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("handleMethodArgumentNotValidException : {}",e);
        return ResponseHandler.toResponse(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE,e.getBindingResult()));
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<Object> handleBindException(BindException e){
        log.error("handleBindException : {}",e.getBindingResult());
        return ResponseHandler.toResponse(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE,e.getBindingResult()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        log.error("handleMethodArgumentTypeMismatchException : {}",e);
        return ResponseHandler.toResponse(ErrorResponse.of(e));
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<Object> handleCustomException(CustomException e){
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
