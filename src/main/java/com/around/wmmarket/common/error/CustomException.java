package com.around.wmmarket.common.error;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String msg;

    public CustomException(ErrorCode errorCode){
        this.errorCode=errorCode;
        this.msg=null;
    }
    public CustomException(ErrorCode errorCode,String msg){
        this.errorCode=errorCode;
        this.msg=msg;
    }
}
