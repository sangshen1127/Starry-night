package com.sangshen.controller.adminController;
import com.sangshen.service.admin.AdminCommentService;
import com.sangshen.entity.PageResult;
import com.sangshen.entity.Result;
import com.sangshen.entity.admin.AdminCommentItem;
import com.sangshen.entity.admin.AdminCommentQueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/comments")
public class AdminCommentController {
    private final AdminCommentService adminCommentService;
    //展示评论
    @GetMapping
    public Result show(AdminCommentQueryParam adminCommentQueryParam){
        PageResult<AdminCommentItem> pageResult = adminCommentService.showComment(adminCommentQueryParam);
        return Result.success(pageResult);
    }
    //审核评论
    @PatchMapping("/{id}/status")
    public Result check(@PathVariable Long id){
        adminCommentService.checkComment(id);
        return Result.success("审核通过^o^y");
    }
    //删除评论
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id){
        adminCommentService.deleteComment(id);
        return Result.success("删除成功(○｀ 3′○)");
    }
}
