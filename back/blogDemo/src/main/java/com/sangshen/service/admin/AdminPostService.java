package com.sangshen.service.admin;

import com.sangshen.entity.PageResult;
import com.sangshen.entity.Post;
import com.sangshen.entity.admin.AdminAddPostItem;
import com.sangshen.entity.admin.AdminPostItem;
import com.sangshen.entity.admin.AdminPostQueryParam;
import jakarta.validation.Valid;

public interface AdminPostService {
    PageResult<AdminPostItem> showPosts(AdminPostQueryParam adminPostQueryParam);

    Post postDetail(Long id);

    Long addPost(AdminAddPostItem adminAddPostItem);

    void updatePost(Long id, @Valid AdminAddPostItem adminAddPostItem);

    void updateStatus(Long id, String status);

    void deletePost(Long id);
}
