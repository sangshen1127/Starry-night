package com.sangshen.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sangshen.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    @Update("update posts set view_count = view_count + 1 where id = #{id}")
    void updateViewCount(Long id);
}
