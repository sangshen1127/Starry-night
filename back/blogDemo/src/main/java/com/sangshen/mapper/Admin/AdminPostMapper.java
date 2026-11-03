package com.sangshen.mapper.Admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangshen.entity.Post;
import com.sangshen.entity.admin.AdminAddPostItem;
import com.sangshen.entity.admin.AdminPostItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminPostMapper extends BaseMapper<Post> {
    //更新文章
    @Update("""
        update posts
        set title        = #{adminAddPostItem.title},
            summary      = #{adminAddPostItem.summary},
            cover_url    = #{adminAddPostItem.coverUrl},
            content      = #{adminAddPostItem.content},
            status       = #{adminAddPostItem.status},
            published_at = case
                when #{adminAddPostItem.status} = 'published' and published_at is null
                    then now()
                else published_at
            end,
            updated_at   = now()
        where id = #{id}
        """)
    public void updatePost(@Param("id") Long id, @Param("adminAddPostItem") AdminAddPostItem adminAddPostItem);

    @Update("""
        update posts
        set status       = #{status},
            published_at = case
                when #{status} = 'published' and published_at is null
                    then now()
                else published_at
            end,
            updated_at   = now()
        where id = #{id}
        """)
    public void updateStatus(@Param("id") Long id, @Param("status") String status);
}
