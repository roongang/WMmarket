package com.around.wmmarket.common.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private HttpStatus status;
    private String code;
    private List<FieldError> errors; // validation 용

    private ErrorResponse(ErrorCode errorCode,List<FieldError> errors){
        this.timestamp=LocalDateTime.now();
        this.message=errorCode.getMessage();
        this.status=errorCode.getStatus();
        this.code=errorCode.getCode();
        this.errors=errors;
    }

    private ErrorResponse(ErrorCode errorCode){
        this.timestamp=LocalDateTime.now();
        this.message=errorCode.getMessage();
        this.status=errorCode.getStatus();
        this.code=errorCode.getCode();
        this.errors=new ArrayList<>(); // [] 이 돼야함
    }

    private ErrorResponse(ErrorCode errorCode,String message){
        this.timestamp=LocalDateTime.now();
        this.message=message;
        this.status=errorCode.getStatus();
        this.code=errorCode.getCode();
        this.errors=new ArrayList<>();
    }

    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult){
        return new ErrorResponse(errorCode,FieldError.of(bindingResult));
    }

    public static ErrorResponse of(ErrorCode errorCode){
        return new ErrorResponse(errorCode);
    }

    public static ErrorResponse of(ErrorCode errorCode,List<FieldError> fieldErrors){
        return new ErrorResponse(errorCode,fieldErrors);
    }

    public static ErrorResponse of(MethodArgumentTypeMismatchException e){
        String value=e.getValue()==null?"":e.getValue().toString();
        List<ErrorResponse.FieldError> fieldErrors=ErrorResponse.FieldError.of(e.getName(),value,e.getErrorCode());
        return new ErrorResponse(ErrorCode.INVALID_TYPE_VALUE,fieldErrors);
    }

    public static ErrorResponse of(ErrorCode errorCode,String message){
        return new ErrorResponse(errorCode,message);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class FieldError{
        private String field;
        private String value;
        private String reason;

        public static List<FieldError> of(String field,String value,String reason){
            List<FieldError> fieldErrors=new ArrayList<>();
            fieldErrors.add(new FieldError(field,value,reason));
            return fieldErrors;
        }

        public static List<FieldError> of(BindingResult bindingResult){
            List<org.springframework.validation.FieldError> fieldErrors=bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(fieldError -> new FieldError(
                            fieldError.getField(),
                            fieldError.getRejectedValue()==null?"":fieldError.getRejectedValue().toString(),
                            fieldError.getDefaultMessage()
                    ))
                    .collect(Collectors.toList());
        }
    }
}
