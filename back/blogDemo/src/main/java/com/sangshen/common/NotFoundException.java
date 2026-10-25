package com.sangshen.common;

//404异常，资源未找到，空指针异常，统一处理
public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);
    }
}
