package com.sangshen.Service;

import com.sangshen.entity.PostListItem;
import com.sangshen.entity.PageResult;
import com.sangshen.entity.PostQueryParam;

public interface PostService {
    PageResult<PostListItem> showPosts(PostQueryParam postQueryParam);
}
