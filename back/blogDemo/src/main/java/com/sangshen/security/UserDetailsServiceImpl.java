package com.sangshen.security;
import com.sangshen.entity.User;
import com.sangshen.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    //拿到当前用户名这个人的具体信息，方便以后的调用
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查询用户
        User user = userMapper.selectUserByName(username);

        //若用户不存在
        if(user == null){
            throw new UsernameNotFoundException("用户不存在：" + username);
        }
        return org.springframework.security.core.userdetails.User   //Security原生User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities("ROLE_ADMIN")  //权限
                .build();
    }
}
