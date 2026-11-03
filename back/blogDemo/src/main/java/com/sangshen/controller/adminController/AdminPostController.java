package com.sangshen.controller.adminController;
import com.sangshen.service.admin.AdminPostService;
import com.sangshen.entity.PageResult;
import com.sangshen.entity.Post;
import com.sangshen.entity.Result;
import com.sangshen.entity.admin.AdminAddPostItem;
import com.sangshen.entity.admin.AdminPostItem;
import com.sangshen.entity.admin.AdminPostQueryParam;
import com.sangshen.entity.admin.AdminPostStatusRequest;
import com.sangshen.entity.admin.IdResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/admin/posts")
@RestController
@RequiredArgsConstructor
public class AdminPostController {
    private final AdminPostService adminPostService;
    //获取文章列表
    @GetMapping
    public Result show(AdminPostQueryParam adminPostQueryParam){
       PageResult<AdminPostItem> postPageResult =  adminPostService.showPosts(adminPostQueryParam);
        return Result.success(postPageResult);
    }

    //文章详情
    @GetMapping("/{id}")
    public Result detail(@PathVariable Long id){
        log.info("当前文章id:{}",id);
        Post post = adminPostService.postDetail(id);
        return Result.success(post);
    }

    //添加文章
    @PostMapping
    public Result add(@Valid @RequestBody AdminAddPostItem adminAddPostItem){
        log.info("添加一个新文章");
        if (!StringUtils.hasText(adminAddPostItem.getStatus())) {
            adminAddPostItem.setStatus("draft");
        }
        Long postId = adminPostService.addPost(adminAddPostItem);

        return Result.success("文章添加成功",new IdResponse(postId));
    }

    //更新文章
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id,
                         @Valid @RequestBody AdminAddPostItem adminAddPostItem){
        adminPostService.updatePost(id,adminAddPostItem);
        return Result.success("更新成功^o^y");
    }

    //发布 / 转草稿
    @PatchMapping("/{id}/status")
    public Result updateStatus(@PathVariable Long id,
                               @Valid @RequestBody AdminPostStatusRequest request){
        adminPostService.updateStatus(id, request.getStatus());
        return Result.success("published".equals(request.getStatus()) ? "已发布" : "已转为草稿");
    }

    //删除文章
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id){
        log.info("删除文章Id：{}",id);
        adminPostService.deletePost(id);
        return Result.success("删除成功(○｀ 3′○)");
    }
}
