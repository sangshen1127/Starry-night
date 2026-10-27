package com.sangshen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangshen.entity.Comment;
import com.sangshen.entity.dto.HomeComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//公开展示评论Mapper
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    //获取首页展示评论
    @Select("""
        SELECT c.id,
               c.post_id,
               p.title AS post_title,
               c.nickname,
               c.website,
               c.content,
               c.created_at
        FROM comments c
        JOIN posts p ON p.id = c.post_id
        WHERE c.status = 'approved'
          AND p.status = 'published'
        ORDER BY c.created_at DESC, c.id DESC
        LIMIT #{limit}
        """)
    List<HomeComment> selectLatestApproved(@Param("limit") int limit);
}
