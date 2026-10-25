package com.sangshen.Service.impl;
import com.sangshen.Service.CommentService;
import com.sangshen.common.NotFoundException;
import com.sangshen.entity.Comment;
import com.sangshen.entity.Post;
import com.sangshen.entity.dto.CommentCreateRequest;
import com.sangshen.mapper.CommentMapper;
import com.sangshen.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    //提交评论
    @Override
    public void applyComment(CommentCreateRequest commentCreateRequest,String ip,String userAgent) {
        Post post = postMapper.selectById(commentCreateRequest.getPostId());
        if(post == null || !"published".equals(post.getStatus())){
            throw new NotFoundException("当前文章不存在");
        }

        //如果60秒内重复提交，后期补上吧

        Comment comment = new Comment();
        comment.setPostId(commentCreateRequest.getPostId());
        comment.setNickname(commentCreateRequest.getNickname().trim());
        comment.setEmail(commentCreateRequest.getEmail());
        comment.setWebsite(normalizeUrl(commentCreateRequest.getWebsite()));
        comment.setContent(commentCreateRequest.getContent());
        comment.setStatus("pending");
        comment.setIp(ip);
        comment.setUserAgent(userAgent);

        commentMapper.insert(comment);
    }



    //判断网站是否合法
    private String normalizeUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }

        String trimmed = url.trim();    //去除首位空格

        //startsWith这个方法的作用：判断字符串是否以指定前缀开始
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            throw new IllegalArgumentException("网站地址必须以 http:// 或 https:// 开头");
        }

        if (trimmed.length() > 255) {
            throw new IllegalArgumentException("网站地址过长");
        }

        return trimmed;
    }
}
