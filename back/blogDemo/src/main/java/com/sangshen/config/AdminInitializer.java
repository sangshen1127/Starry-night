package com.sangshen.config;

import com.sangshen.entity.User;
import com.sangshen.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//初始化管理员账号
@Slf4j
@Component
@RequiredArgsConstructor    //自动生成构造方法
public class AdminInitializer implements CommandLineRunner {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    //从yml里拿配置好的管理员账号和密码
    @Value("${blog.admin.username}")
    private String adminUsername;
    @Value("${blog.admin.password}")
    private String adminPassword;
    @Override
    public void run(String... args) throws Exception {
        Long count = userMapper.selectCount(null);
        if(count != null && count > 0){
            return;
        }

        //组装管理员
        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));//密码加密
        admin.setNickname("桑葚树下泡枸杞");

        //入库
        userMapper.insert(admin);

        log.info("已创建初始管理员账号：{}",adminUsername);
    }
}
