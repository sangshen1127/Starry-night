package com.sangshen.common;

import com.sangshen.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //非法参数异常的全局捕捉，这只能捕获进入Controller之后的异常
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result> handle(IllegalArgumentException iae){
        log.error("非法参数异常：{}", iae.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error(iae.getMessage()));  //返回400的状态码
    }

    //资源未找到异常的全局捕捉
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Result> handle(NotFoundException nfe){
        log.error("资源未找到异常：{}", nfe.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Result.error(nfe.getMessage()));
    }

    //防止请求太多，重复提交的异常捕捉
    @ExceptionHandler(TooManyRequestException.class)
    public ResponseEntity<Result> handle(TooManyRequestException tre){
        log.warn("请求过于频繁：{}", tre.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)  //返回429的状态码
                .body(Result.error(tre.getMessage()));
    }
}