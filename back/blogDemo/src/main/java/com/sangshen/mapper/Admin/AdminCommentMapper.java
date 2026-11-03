package com.sangshen.mapper.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sangshen.entity.Comment;
import com.sangshen.entity.admin.AdminCommentItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AdminCommentMapper extends BaseMapper<Comment> {

    List<AdminCommentItem> selectAdminComments(Page<Comment> page,
                                               @Param("status") String status,
                                               @Param("postId") Long postId);

    @Update("update comments set status = 'approved' where id = #{id}")
    void updateCommentStatus(Long id);
}
