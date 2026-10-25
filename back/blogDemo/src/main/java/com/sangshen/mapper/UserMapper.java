package com.sangshen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangshen.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    //按照用户名查找用户
    @Select("select id, username, password_hash, nickname, created_at, updated_at from users where username = #{username}")
    User selectUserByName(String username);
}
