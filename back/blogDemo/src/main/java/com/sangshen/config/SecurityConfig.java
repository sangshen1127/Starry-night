package com.sangshen.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    // 配置安全过滤器链
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/api/**","/error").permitAll()    //当前路径下的所有请求都放行
                                    .anyRequest().authenticated()   //其他所有请求都需要认证
                );
        return httpSecurity.build();
    }
}
