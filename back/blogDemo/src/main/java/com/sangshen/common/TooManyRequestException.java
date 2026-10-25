package com.sangshen.common;

//防止重复提交请求
public class TooManyRequestException extends RuntimeException{
    public TooManyRequestException(String Message){
        super(Message);
    }
}
