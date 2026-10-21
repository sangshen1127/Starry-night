package com.sangshen.controller;

import com.sangshen.Service.PostService;
import com.sangshen.entity.PageResult;
import com.sangshen.entity.PostListItem;
import com.sangshen.entity.PostQueryParam;
import com.sangshen.entity.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor    // 自动生成构造函数
public class PostController {
    private final PostService postService;
    //分页展示已发布的文章
    @GetMapping("/posts")
    public Result show(PostQueryParam postQueryParam){
        if( postQueryParam.getPage() == null || postQueryParam.getPage() < 1){
            throw new IllegalArgumentException("页码不能够小于1");
        }

        if(postQueryParam.getSize() == null || postQueryParam.getSize() < 1 || postQueryParam.getSize() > 50){
            throw new IllegalArgumentException("每页数量不能够小于1或者大于50");
        }
        PageResult<PostListItem> postListItemPageResult = postService.showPosts(postQueryParam);
        return Result.success(postListItemPageResult);
    }
}
