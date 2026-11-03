package com.sangshen.mapper.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangshen.entity.FriendLink;
import com.sangshen.entity.admin.AdminAddFriendLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AdminFriendLinkMapper extends BaseMapper<FriendLink> {
    //直接返回全部友链
    @Select("select id, name, url, avatar_url, description, sort_order, visible, created_at, updated_at from friend_links order by sort_order ,id ")
    List<FriendLink> showFriendLinks();

    @Select("select exists(select 1 from friend_links where url = #{url})")
    boolean existsUrl(String url);

    @Update("update friend_links set " +
            "name = #{adminAddFriendLink.name}, " +
            "url = #{adminAddFriendLink.url}, " +
            "avatar_url = #{adminAddFriendLink.avatarUrl}, " +
            "description = #{adminAddFriendLink.description}, " +
            "sort_order = #{adminAddFriendLink.sortOrder}, " +
            "visible = #{adminAddFriendLink.visible}, " +
            "updated_at = now() " +
            "where id = #{id}")
    void updateFriendLink(@Param("id") Long id, @Param("adminAddFriendLink") AdminAddFriendLink adminAddFriendLink);
}
