package com.sangshen.controller.adminController;
import com.sangshen.service.admin.AdminFriendLinkService;
import com.sangshen.entity.FriendLink;
import com.sangshen.entity.Result;
import com.sangshen.entity.admin.AdminAddFriendLink;
import com.sangshen.entity.admin.IdResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/friend-links")
public class AdminFriendLinkController {
    private final AdminFriendLinkService adminFriendLinkService;

    //展示友链
    @GetMapping
    public Result show(){
        List<FriendLink> friendLinks = adminFriendLinkService.showFriendLinks();
        return Result.success(friendLinks);
    }

    //添加友链
    @PostMapping
    public Result add(@RequestBody AdminAddFriendLink adminAddFriendLink){
        Long friendLinkId = adminFriendLinkService.addFriendLink(adminAddFriendLink);
        return Result.success(new IdResponse(friendLinkId));
    }

    //更新友链
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody AdminAddFriendLink adminAddFriendLink){
        adminFriendLinkService.updateFriendLink(id,adminAddFriendLink);
        return Result.success("更新成功");
    }
    //删除友链
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id){
        adminFriendLinkService.deleteFriendLink(id);
        return Result.success("删除成功");
    }
}
