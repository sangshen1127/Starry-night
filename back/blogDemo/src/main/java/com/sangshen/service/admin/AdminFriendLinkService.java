package com.sangshen.service.admin;

import com.sangshen.entity.FriendLink;
import com.sangshen.entity.admin.AdminAddFriendLink;

import java.util.List;

public interface AdminFriendLinkService {
    List<FriendLink> showFriendLinks();

    Long addFriendLink(AdminAddFriendLink adminAddFriendLink);

    void updateFriendLink(Long id, AdminAddFriendLink adminAddFriendLink);

    void deleteFriendLink(Long id);
}
