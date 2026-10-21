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
    //非法参数异常
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result> handle(IllegalArgumentException iae){
        log.error("非法参数异常：{}", iae.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error(iae.getMessage()));
    }
}
