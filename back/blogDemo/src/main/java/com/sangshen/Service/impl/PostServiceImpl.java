package com.sangshen.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sangshen.Service.PostService;
import com.sangshen.common.NotFoundException;
import com.sangshen.entity.*;
import com.sangshen.entity.dto.PostDetail;
import com.sangshen.entity.dto.PublicComment;
import com.sangshen.mapper.CommentMapper;
import com.sangshen.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    @Override
    public PageResult<PostListItem> showPosts(PostQueryParam postQueryParam) {
        //建立分页对象
        Page<Post> page = new Page<>(postQueryParam.getPage(), postQueryParam.getSize());
        //建条件构造器
        LambdaQueryWrapper<Post> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Post::getStatus,"published") //发布状态为已发布
                /*第一个参数：判断用户到底传没传内容，若为null或空字符串直接返回false
                * 第二个参数：指定对数据可的哪一个字段进行模糊查询
                * 第三个参数：指定要查询的内容
                * */
                .like(StringUtils.hasText(postQueryParam.getKeyword()), Post::getTitle, postQueryParam.getKeyword())  //
                .orderByDesc(Post::getPublishedAt)    //根据发布日期降序排序
                .orderByDesc(Post::getId);//若发布日期相同，则根据i的降序排序
        //执行查询
        Page<Post> postPage = postMapper.selectPage(page, lambdaQueryWrapper);
        //将查询结果逐条转换为PostListItem，这里流的map调用PostListItem的of方法将post一个个转换为PostListItem
        List<PostListItem> list = postPage.getRecords().stream().map(PostListItem::of).toList();
        return new PageResult<>(list,postPage.getTotal(),postPage.getCurrent(),postPage.getSize());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PostDetail showPostDetail(Long id) {
        Post post = postMapper.selectById(id);
        if(post == null || !"published".equals(post.getStatus())){
            throw new NotFoundException("当前文章不存在");
        }
        //浏览量增加
        postMapper.updateViewCount(id);
        post.setViewCount(post.getViewCount() + 1);
        return PostDetail.of(post);
    }

    //展示文章评论
    @Override
    public PageResult<PublicComment> showComments(Long id, CommentQueryParam commentQueryParam) {
        //判断当前文章是否存在
        Post post = postMapper.selectById(id);
        if(post == null || !"published".equals(post.getStatus())){
            throw new NotFoundException("当前文章不存在");
        }

        //建立分页对象
        Page<com.sangshen.entity.Comment> page = new Page<>(commentQueryParam.getPage(), commentQueryParam.getSize());
        //建立条件构造器
        LambdaQueryWrapper<com.sangshen.entity.Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.sangshen.entity.Comment::getPostId, commentQueryParam.getId())
                .eq(com.sangshen.entity.Comment::getStatus, "approved")
                .orderByDesc(com.sangshen.entity.Comment::getCreatedAt)
                .orderByDesc(com.sangshen.entity.Comment::getId);
            //执行查询
        Page<com.sangshen.entity.Comment> pageComment = commentMapper.selectPage(page,wrapper);
        //将查询的结果逐条转换为ShowComment
        List<PublicComment> list = pageComment.getRecords().stream().map(PublicComment::of).toList();

        return new PageResult<>(list, pageComment.getTotal(), pageComment.getCurrent(), pageComment.getSize());
    }
}
