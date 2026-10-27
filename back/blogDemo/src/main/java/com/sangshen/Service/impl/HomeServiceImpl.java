package com.sangshen.Service.impl;

import com.sangshen.Service.HomeService;
import com.sangshen.entity.PostListItem;
import com.sangshen.entity.dto.FriendLinkDto;
import com.sangshen.entity.dto.Home;
import com.sangshen.entity.dto.HomeComment;
import com.sangshen.mapper.CommentMapper;
import com.sangshen.mapper.FriendLinkMapper;
import com.sangshen.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
    private final PostMapper postMapper;
    private final CommentMapper  commentMapper;
    private final FriendLinkMapper friendLinkMapper;
    @Override
    public Home homeShow() {

        List<PostListItem> lastestPosts = postMapper.showLatestPosts(5)
                                    .stream().map(PostListItem::of).toList();

        List<HomeComment> latestComments = commentMapper.selectLatestApproved(5);

        List<FriendLinkDto> friendLinkDtoList = friendLinkMapper.listWhereVisible()
                                            .stream().map(FriendLinkDto::of).toList();
        return new Home(lastestPosts, latestComments, friendLinkDtoList);
    }
}
