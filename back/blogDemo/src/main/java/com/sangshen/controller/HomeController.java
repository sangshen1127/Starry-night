package com.sangshen.controller;


import com.sangshen.Service.HomeService;
import com.sangshen.entity.Result;
import com.sangshen.entity.dto.Home;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;
    @GetMapping
    public Result Home(){
        log.info("首页展示");
        Home home = homeService.homeShow();
        return Result.success(home);
    }
}
