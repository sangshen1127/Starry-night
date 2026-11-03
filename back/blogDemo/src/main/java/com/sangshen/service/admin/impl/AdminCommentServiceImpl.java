package com.sangshen.service.admin.impl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sangshen.service.admin.AdminCommentService;
import com.sangshen.entity.Comment;
import com.sangshen.entity.PageResult;
import com.sangshen.entity.admin.AdminCommentItem;
import com.sangshen.entity.admin.AdminCommentQueryParam;
import com.sangshen.mapper.Admin.AdminCommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {
    private final AdminCommentMapper adminCommentMapper;
    //展示当前评论
    @Override
    public PageResult<AdminCommentItem> showComment(AdminCommentQueryParam adminCommentQueryParam) {
        adminCommentQueryParam.normalize();
        Page<Comment> page = new Page<>(adminCommentQueryParam.getPage(),adminCommentQueryParam.getSize());
        List<AdminCommentItem> list = adminCommentMapper.selectAdminComments(page,adminCommentQueryParam.getStatus(),
                adminCommentQueryParam.getPostId());

        return new PageResult<>(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    //审核评论
    @Override
    public void checkComment(Long id) {
        adminCommentMapper.updateCommentStatus(id);
    }

    //删除评论
    @Override
    public void deleteComment(Long id) {
        adminCommentMapper.deleteById(id);
    }
}
