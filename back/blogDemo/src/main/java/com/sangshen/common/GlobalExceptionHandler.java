package com.sangshen.common;

import com.sangshen.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    //兜底未知异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> handle(Exception e){
        log.error("未捕获异常",e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error("服务器开小差了，请稍后再试"));
    }


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

    //请求参数报错
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result> handle(MethodArgumentNotValidException e){
        log.error("请求参数报错");
        String msg = e.getBindingResult()   // 取出装着所有校验错误的 BindingResult
                .getFieldErrors().stream()
                .map(f -> f.getField() + " " + f.getDefaultMessage())
                .findFirst()
                .orElse("参数校验失败");
        return ResponseEntity.badRequest().body(Result.error(msg));
    }

    //文件超过大小限制异常
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result> handle(MaxUploadSizeExceededException mse){
        log.error("当前文件过大");
        return ResponseEntity.badRequest().body(Result.error("当前文件过大"));
    }
}