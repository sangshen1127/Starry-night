package com.sangshen.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

//拦截未登录或登录已过期的信息
//拦截认证失败发生在过滤层的异常，像请求失败这一类的请求，在过滤器层就被拦截
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    //设置状态码为401
        response.setContentType("application/json;charset=UTF-8");    //设置响应内容类型为JSON
        response.getWriter().write(
                "{\"code\":0,\"message\":\"未登录或登录已过期\",\"data\":null}");    //返回JSON格式的错误信息
    }
}