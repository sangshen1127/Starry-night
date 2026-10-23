package com.sangshen.controller;

import com.sangshen.Service.Auth.AuthService;
import com.sangshen.entity.Result;
import com.sangshen.entity.User;
import com.sangshen.entity.dto.LoginRequest;
import com.sangshen.entity.dto.UserInfo;
import com.sangshen.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor    // 生成构造函数，包含所有 final 属性
public class AuthController {
    private final AuthService authService;
    private final SecurityContextRepository securityContextRepository;
    private final UserMapper userMapper;
    //登录功能
    @PostMapping("/login")
    public ResponseEntity<Result> login(@Valid @RequestBody LoginRequest request,
                        HttpServletRequest httpRequest,
                        HttpServletResponse httpResponse){ //@Valid告诉框架这个对象/参数执行校验
        log.info("登录功能");
        try {
            //用户认证，生成token
            Authentication authentication = authService.login(
                    request.getUsername(), request.getPassword());

            //新建一个空的上下文，将得到的用户信息存入上下文
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);  // 将认证信息存入上下文
            SecurityContextHolder.setContext(context);  // 绑定到当前线程
            securityContextRepository.saveContext(context, httpRequest, httpResponse);  // 保存上下文，方便之后接口验证

            User user = userMapper.selectUserByName(request.getUsername());
            UserInfo info = new UserInfo(user.getId(), user.getUsername(), user.getNickname());
            return ResponseEntity.ok(Result.success(info)); // 返回结果

        } catch (AuthenticationException e) {
            log.warn("登录失败: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error("用户名或密码错误"));
        }
    }
}
