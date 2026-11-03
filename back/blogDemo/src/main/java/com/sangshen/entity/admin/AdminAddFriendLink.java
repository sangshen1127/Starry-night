package com.sangshen.entity.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminAddFriendLink {
    private String name;
    private String url;
    private String avatarUrl;
    private String description;
    private Integer sortOrder;  //排序，越小越靠前
    private Boolean visible;
}
