package com.sangshen.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sangshen.service.admin.AdminCommonService;
import com.sangshen.entity.Comment;
import com.sangshen.entity.Post;
import com.sangshen.entity.admin.AdminAllCounts;
import com.sangshen.mapper.CommentMapper;
import com.sangshen.mapper.FriendLinkMapper;
import com.sangshen.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminCommonServiceImpl implements AdminCommonService {

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final FriendLinkMapper friendLinkMapper;
    @Override
    public AdminAllCounts showAllCounts() {
        long postCount = postMapper.selectCount(null);

        long draftCount = postMapper.selectCount(
                new LambdaQueryWrapper<Post>().eq(Post::getStatus, "draft"));

        long commentCount = commentMapper.selectCount(null);

        long pendingCommentCount = commentMapper.selectCount(
                new LambdaQueryWrapper<Comment>().eq(Comment::getStatus, "pending"));

        long friendLinkCount = friendLinkMapper.selectCount(null);

        return new AdminAllCounts(postCount, draftCount, commentCount,
                pendingCommentCount, friendLinkCount);
    }
}
