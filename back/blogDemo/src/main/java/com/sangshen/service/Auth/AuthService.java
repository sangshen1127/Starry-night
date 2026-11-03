package com.sangshen.service.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    // 注入 AuthenticationManager.来验证身份
    private final AuthenticationManager authenticationManager;
    //注入防止重复请求的类
    private final LoginAttemptService loginAttemptService;

    //这里返回Authentication对象，在这个对象中包含着用户的信息与权限
    public Authentication login(String username, String password, String ip){
        //先验证用户是否被软封禁
        loginAttemptService.ensureAllowed(ip);
        // 验证用户名和密码
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            //登录成功，清理失败请求
            loginAttemptService.clear(ip);
            return authentication;
        } catch (AuthenticationException e) {
            //记录登录失败时间戳
            loginAttemptService.recordFailure(ip,username);
            throw e;
        }
    }
}