package com.sangshen.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
//博客列表项，列表页所需要的信息
public class PostListItem {
    private Long id;
    private String title;
    private String summary;
    private String coverUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedAt;
    private Long viewCount;

    //根据Post对象创建PostListItem对象
    public static PostListItem of(Post post){
        return new PostListItem(post.getId(), post.getTitle(), post.getSummary(), post.getCoverUrl(), post.getPublishedAt(), post.getViewCount());
    }
}
