package com.sangshen.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sangshen.entity.FriendLink;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FriendLinkDto {
    private Long id;
    private String name;
    private String url;
    private String avatarUrl;
    private String description;

    public static FriendLinkDto of(FriendLink friendLink){
        return new FriendLinkDto(friendLink.getId(), friendLink.getName(), friendLink.getUrl(), friendLink.getAvatarUrl(), friendLink.getDescription());
    }
}
