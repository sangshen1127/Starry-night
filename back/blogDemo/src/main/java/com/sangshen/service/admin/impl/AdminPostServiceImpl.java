package com.sangshen.service.admin.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sangshen.service.admin.AdminPostService;
import com.sangshen.common.NotFoundException;
import com.sangshen.entity.PageResult;
import com.sangshen.entity.Post;
import com.sangshen.entity.admin.AdminAddPostItem;
import com.sangshen.entity.admin.AdminPostItem;
import com.sangshen.entity.admin.AdminPostQueryParam;
import com.sangshen.mapper.Admin.AdminPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPostServiceImpl implements AdminPostService {
    private final AdminPostMapper adminPostMapper;
    //文章列表
    @Override
    public PageResult<AdminPostItem> showPosts(AdminPostQueryParam adminPostQueryParam) {
        //控制检查
        adminPostQueryParam.normalize();
        //创建分页工具
        Page<Post> page = new Page<>(adminPostQueryParam.getPage(),adminPostQueryParam.getSize());
        //建立条件构造器
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        //sql语句
        wrapper.select(Post::getId, Post::getTitle, Post::getStatus,
                        Post::getPublishedAt, Post::getCreatedAt,
                        Post::getUpdatedAt, Post::getViewCount)
                .eq(StringUtils.hasText(adminPostQueryParam.getStatus()), Post::getStatus, adminPostQueryParam.getStatus())
                .like(StringUtils.hasText(adminPostQueryParam.getKeyword()), Post::getTitle, adminPostQueryParam.getKeyword())
                .orderByDesc(Post::getCreatedAt)
                .orderByDesc(Post::getId);
        List<Post> post = adminPostMapper.selectList(page,wrapper);
        //将集合中的数据转换为AdminPostItem
        List<AdminPostItem> list = post.stream().map(AdminPostItem::of).toList();

        return new PageResult<>(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    //文章详情
    @Override
    public Post postDetail(Long id) {
        Post post = adminPostMapper.selectById(id);
        if(post == null){
            throw new NotFoundException("当前文章不存在");
        }
        return post;
    }

    //添加文章
    @Override
    public Long addPost(AdminAddPostItem adminAddPostItem) {
        String status = normalizeStatus(adminAddPostItem.getStatus());
        adminAddPostItem.setStatus(status);
        if (!StringUtils.hasText(adminAddPostItem.getTitle())) {
            throw new IllegalArgumentException("文章标题不得为空");
        }
        if (!StringUtils.hasText(adminAddPostItem.getContent())) {
            throw new IllegalArgumentException("文章内容不得为空");
        }

        Post post = new Post();
        post.setTitle(adminAddPostItem.getTitle().trim());
        post.setSummary(adminAddPostItem.getSummary());
        post.setCoverUrl(adminAddPostItem.getCoverUrl());
        post.setContent(adminAddPostItem.getContent());
        post.setStatus(status);
        //状态为发布则直接记录发布时间，负责记录为null
        if ("published".equals(status)) {
            post.setPublishedAt(LocalDateTime.now());
            post.setCreatedAt(LocalDateTime.now());
        } else {
            post.setPublishedAt(null);
            post.setUpdatedAt(LocalDateTime.now());
        }
        post.setViewCount(0L);
        adminPostMapper.insert(post);
        return post.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    //更新文章
    @Override
    public void updatePost(Long id, AdminAddPostItem adminAddPostItem) {
        //判断当前文章是否为空
        Post post = adminPostMapper.selectById(id);
        if(post == null){
            throw new NotFoundException("当前文章不存在");
        }
        if (!StringUtils.hasText(adminAddPostItem.getTitle())) {
            throw new IllegalArgumentException("文章标题不得为空");
        }
        if (!StringUtils.hasText(adminAddPostItem.getContent())) {
            throw new IllegalArgumentException("文章内容不得为空");
        }
        adminAddPostItem.setStatus(normalizeStatus(adminAddPostItem.getStatus()));
        adminAddPostItem.setTitle(adminAddPostItem.getTitle().trim());
        adminPostMapper.updatePost(id,adminAddPostItem);
    }

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return "draft";
        }
        if (!"draft".equals(status) && !"published".equals(status)) {
            throw new IllegalArgumentException("文章状态只能是 draft 或 published");
        }
        return status;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String status) {
        if (adminPostMapper.selectById(id) == null) {
            throw new NotFoundException("当前文章不存在");
        }
        adminPostMapper.updateStatus(id, normalizeStatus(status));
    }

    @Override
    public void deletePost(Long id) {
        if (adminPostMapper.selectById(id) == null) {
            throw new NotFoundException("当前文章不存在");
        }
        adminPostMapper.deleteById(id);
    }
}
