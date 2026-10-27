package com.sangshen.controller;

import com.sangshen.Service.FriendLinkService;
import com.sangshen.entity.FriendLink;
import com.sangshen.entity.Result;
import com.sangshen.entity.dto.FriendLinkDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//友链控制器
@Slf4j
@RestController
@RequestMapping("/api/friend-links")
@RequiredArgsConstructor
public class FriendLinksController {
    private final FriendLinkService friendLinkService;
    //友链列表
    @GetMapping
    public Result listFriendLinks(){
        log.info("友链列表");
        List<FriendLinkDto> list = friendLinkService.list();
        return Result.success(list);
    }
}
