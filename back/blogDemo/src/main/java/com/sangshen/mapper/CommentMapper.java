package com.sangshen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangshen.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

//公开展示评论Mapper
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
