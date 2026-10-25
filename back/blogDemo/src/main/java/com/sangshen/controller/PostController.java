package com.sangshen.controller;

import com.sangshen.Service.PostService;
import com.sangshen.entity.*;
import com.sangshen.entity.dto.PostDetail;
import com.sangshen.entity.dto.PublicComment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor    // 自动生成构造函数
public class PostController {
    private final PostService postService;
    //分页展示已发布的文章
    @GetMapping
    public Result show(PostQueryParam postQueryParam){
        //若前端传空值过来,给定默认值
        postQueryParam.normalize();
        PageResult<PostListItem> postListItemPageResult = postService.showPosts(postQueryParam);
        return Result.success(postListItemPageResult);
    }

    //展示文章详情
    @GetMapping("/{id}")
    public Result showPostDetail(@PathVariable Long id){
        log.info("当前文章id：{}",id);
        PostDetail postDetail = postService.showPostDetail(id);
        return Result.success(postDetail);
    }

    //评论展示
    @GetMapping("/{id}/comments")
    public Result showComments(@PathVariable Long id, CommentQueryParam commentQueryParam){
        log.info("当前评论文章id：{}",id);
        commentQueryParam.normalize();
        PageResult<PublicComment> pageResult = postService.showComments(id,commentQueryParam);
        return Result.success(pageResult);
    }
}
