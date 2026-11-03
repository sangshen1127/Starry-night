package com.sangshen.service.admin.impl;
import com.sangshen.service.admin.AdminFriendLinkService;
import com.sangshen.common.NotFoundException;
import com.sangshen.common.TooManyRequestException;
import com.sangshen.entity.FriendLink;
import com.sangshen.entity.admin.AdminAddFriendLink;
import com.sangshen.mapper.Admin.AdminFriendLinkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminFriendLinkServiceImpl implements AdminFriendLinkService {
    private final AdminFriendLinkMapper adminFriendLinkMapper;
    //展示当前友链
    @Override
    public List<FriendLink> showFriendLinks() {
        return adminFriendLinkMapper.showFriendLinks();
    }

    //添加友链
    @Override
    public Long addFriendLink(AdminAddFriendLink adminAddFriendLink) {
        //若URL重复返回409错误
        if (adminFriendLinkMapper.existsUrl(adminAddFriendLink.getUrl())){
            throw new TooManyRequestException("当前友链已存在");
        }

        FriendLink friendLink = new FriendLink();
        friendLink.setName(adminAddFriendLink.getName());
        friendLink.setUrl(adminAddFriendLink.getUrl());
        friendLink.setAvatarUrl(adminAddFriendLink.getAvatarUrl());
        friendLink.setDescription(adminAddFriendLink.getDescription());
        friendLink.setSortOrder(adminAddFriendLink.getSortOrder() == null ? 0 : adminAddFriendLink.getSortOrder());
        friendLink.setVisible(adminAddFriendLink.getVisible() == null || adminAddFriendLink.getVisible());
        adminFriendLinkMapper.insert(friendLink);
        return friendLink.getId();
    }

    //更新友链
    @Override
    public void updateFriendLink(Long id, AdminAddFriendLink adminAddFriendLink) {
        FriendLink friendLink = adminFriendLinkMapper.selectById(id);
        //若当前友链不存在，返回404
        if (friendLink == null){
            throw new NotFoundException("当前友链不存在");
        }
        //更新友链信息
        if (adminAddFriendLink.getSortOrder() == null) {
            adminAddFriendLink.setSortOrder(0);
        }
        if (adminAddFriendLink.getVisible() == null) {
            adminAddFriendLink.setVisible(true);
        }
        adminFriendLinkMapper.updateFriendLink(id,adminAddFriendLink);
    }

    //删除友链
    @Override
    public void deleteFriendLink(Long id) {
        if(adminFriendLinkMapper.selectById(id) == null){
            throw new NotFoundException("当前友链不存在");
        }
        adminFriendLinkMapper.deleteById(id);
    }
}
