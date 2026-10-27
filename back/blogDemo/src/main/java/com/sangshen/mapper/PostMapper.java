package com.sangshen.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangshen.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    @Update("update posts set view_count = view_count + 1 where id = #{id}")
    void updateViewCount(Long id);

    //获取最新的一个文章
    @Select("""
        SELECT id, title, summary, cover_url, published_at, view_count
        FROM posts
        WHERE status = 'published'
        ORDER BY published_at DESC, id DESC
        LIMIT #{limit}
        """)
    List<Post> showLatestPosts(@Param("limit") int limit);
}
