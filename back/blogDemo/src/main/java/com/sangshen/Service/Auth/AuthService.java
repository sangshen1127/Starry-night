package com.sangshen.Service.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    // 注入 AuthenticationManager.来验证身份
    private final AuthenticationManager authenticationManager;

    //这里返回Authentication对象，在这个对象中包含着用户的信息与权限
    public Authentication login(String username, String password){
        // 验证用户名和密码
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username,password));
    }
}