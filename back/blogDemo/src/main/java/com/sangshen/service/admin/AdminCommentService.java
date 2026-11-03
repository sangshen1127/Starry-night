package com.sangshen.service.admin;

import com.sangshen.entity.PageResult;
import com.sangshen.entity.admin.AdminCommentItem;
import com.sangshen.entity.admin.AdminCommentQueryParam;

public interface AdminCommentService {
    PageResult<AdminCommentItem> showComment(AdminCommentQueryParam adminCommentQueryParam);

    void checkComment(Long id);

    void deleteComment(Long id);
}
