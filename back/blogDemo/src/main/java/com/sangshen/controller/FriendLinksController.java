package com.sangshen.controller;

import com.sangshen.entity.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//友链控制器
@RestController
@RequestMapping("/api/friend-links")
public class FriendLinksController {
    //友链列表
    @GetMapping
    public Result listFriendLinks(){

        return Result.success();
    }
}
