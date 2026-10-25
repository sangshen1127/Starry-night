package com.sangshen.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Result {
    private Integer code;
    private String message;
    private Object data;

    //成功返回
    public static Result success(){
        Result result = new Result();
        result.code = 1;
        result.message = "success";
        return result;
    }

    public static Result success(Object data){
        Result result = success();
        result.data = data;
        return result;
    }

    //成功返回，带信息版本
    public static Result success(String message){
        Result result = new Result();
        result.code = 1;
        result.message = message;
        return result;
    }
    //失败返回
    public static Result error(String msg){
        Result result = new Result();
        result.code = 0;
        result.message = msg;
        return result;
    }
}
