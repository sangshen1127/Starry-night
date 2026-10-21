package com.sangshen.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sangshen.Service.PostService;
import com.sangshen.entity.Post;
import com.sangshen.entity.PostListItem;
import com.sangshen.entity.PageResult;
import com.sangshen.entity.PostQueryParam;
import com.sangshen.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class postServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer;

    @Override
    public PageResult<PostListItem> showPosts(PostQueryParam postQueryParam) {
        //建立分页对象
        Page<Post> page = new Page<>(postQueryParam.getPage(), postQueryParam.getSize());
        //建条件构造器
        LambdaQueryWrapper<Post> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Post::getStatus,"published") //发布状态为已发布
                /*第一个参数：判断用户到底传没传内容，若为null或空字符串直接返回false
                * 第二个参数：指定对数据可的哪一个字段进行模糊查询
                * 第三个参数：指定要查询的内容
                * */
                .like(StringUtils.hasText(postQueryParam.getKeyword()), Post::getTitle, postQueryParam.getKeyword())  //
                .orderByDesc(Post::getPublishedAt);    //根据发布日期降序排序
        //执行查询
        Page<Post> postPage = postMapper.selectPage(page, lambdaQueryWrapper);
        //将查询结果逐条转换为PostListItem，这里流的map调用PostListItem的of方法将post一个个转换为PostListItem
        List<PostListItem> list = postPage.getRecords().stream().map(PostListItem::of).toList();
        return new PageResult<>(list,postPage.getTotal(),postPage.getCurrent(),postPage.getSize());
    }
}
