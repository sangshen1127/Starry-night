package com.sangshen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangshen.entity.FriendLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FriendLinkMapper extends BaseMapper<FriendLink> {
    @Select("select id, name, url, avatar_url, description, sort_order, visible, created_at, updated_at from friend_links where visible = 1 order by sort_order ")
    List<FriendLink> listWhereVisible();
}
