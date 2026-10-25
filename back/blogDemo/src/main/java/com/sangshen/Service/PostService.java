package com.sangshen.Service;

import com.sangshen.entity.*;
import com.sangshen.entity.dto.PostDetail;
import com.sangshen.entity.dto.PublicComment;

public interface PostService {
    PageResult<PostListItem> showPosts(PostQueryParam postQueryParam);

    PostDetail showPostDetail(Long id);

    PageResult<PublicComment> showComments(Long id, CommentQueryParam commentQueryParam);
}
