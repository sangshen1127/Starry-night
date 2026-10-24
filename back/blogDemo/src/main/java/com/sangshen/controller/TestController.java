package com.sangshen.controller;

import com.sangshen.entity.Post;
import com.sangshen.entity.Result;
import com.sangshen.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestController {
//    @Autowired
//    private PostMapper postMapper;
//    @GetMapping("/test")
//    public Result test(){
//        List<Post> posts = postMapper.selectList(null);
//        return Result.success(posts);
//    }
}
