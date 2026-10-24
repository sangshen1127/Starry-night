package com.sangshen.config;
import com.sangshen.security.RestAccessDeniedHandler;
import com.sangshen.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
public class SecurityConfig {
    // 配置安全过滤器链
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   SecurityContextRepository securityContextRepository,
                                                   RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                                                   RestAccessDeniedHandler restAccessDeniedHandler) throws Exception{
        // 第一版先关掉 CSRF，靠 SameSite 兜底
        httpSecurity
                //指定"登录态存在 Session 里"，读写用同一个仓库
                .securityContext(sc -> sc.securityContextRepository(securityContextRepository))
                .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/api/auth/login").permitAll()
                                        .requestMatchers("/api/auth/me", "/api/auth/logout").authenticated()    //拦截
                                        .requestMatchers("/api/admin/**").authenticated()
                                        .requestMatchers("/api/**", "/error", "/uploads/**").permitAll()    //当前路径下的所有请求都放行
                                        .anyRequest().authenticated()   //其他所有请求都需要认证
                            )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler));
        return httpSecurity.build();
    }

    //认证管理器：登录接口靠他来验明正身
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();  //返回认证管理器
    }

    //登录态仓库，基于Session的存取
    @Bean
    public SecurityContextRepository securityContextRepository(){
        return new HttpSessionSecurityContextRepository();     //基于Session的登录态仓库
    }
}
