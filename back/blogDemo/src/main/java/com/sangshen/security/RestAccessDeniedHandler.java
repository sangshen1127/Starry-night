package com.sangshen.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);   //状态码设置为403
        response.setContentType("application/json;charset=UTF-8");  //设置响应内容类型为JSON
        response.getWriter().write(
                "{\"code\":0,\"message\":\"没有权限访问该资源\",\"data\":null}");  //返回JSON格式的错误信息
    }
}