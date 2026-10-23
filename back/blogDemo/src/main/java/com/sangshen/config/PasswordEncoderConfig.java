package com.sangshen.config;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//
@Configuration
public class PasswordEncoderConfig {
    @Bean   //把这个方法的返回值注册进容器
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); //这是一个用BCrypt方式加密的
    }
}
