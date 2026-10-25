package com.sangshen.controller;
import com.sangshen.Service.CommentService;
import com.sangshen.entity.Result;
import com.sangshen.entity.dto.CommentCreateRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    //提交评论
    @PostMapping
    public Result apply(@Valid @RequestBody CommentCreateRequest req,
                        HttpServletRequest request) {
        commentService.applyComment(req, request.getRemoteAddr(),   // 获取用户IP地址
                request.getHeader("User-Agent"));   // 获取用户代理信息
        return Result.success("评论提交成功，请等待审核通过显示");
    }
}
