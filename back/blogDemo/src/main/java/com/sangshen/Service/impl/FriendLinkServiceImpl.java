package com.sangshen.Service.impl;

import com.sangshen.Service.FriendLinkService;
import com.sangshen.entity.FriendLink;
import com.sangshen.entity.dto.FriendLinkDto;
import com.sangshen.mapper.FriendLinkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendLinkServiceImpl implements FriendLinkService {
    private final FriendLinkMapper friendLinkMapper;
    //获取友链链表
    @Override
    public List<FriendLinkDto> list() {
        List<FriendLink> friendLinks = friendLinkMapper.listWhereVisible();
        return friendLinks.stream().map(FriendLinkDto::of).toList();
    }
}
