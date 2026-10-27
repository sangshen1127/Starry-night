package com.sangshen.entity.dto;


import com.sangshen.entity.PostListItem;

import java.util.List;

//首页所展示的全部内容,record 自带全参构造、getter、equals、hashCode，
public record Home(
    List<PostListItem> latestPosts,
    List<HomeComment> lastestComments,
    List<FriendLinkDto> friendLinks
        ){

}
