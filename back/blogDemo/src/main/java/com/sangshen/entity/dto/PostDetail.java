package com.sangshen.entity.dto;

import com.sangshen.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDetail {
    private Long id;
    private String title;
    private String summary;
    private String coverUrl;
    private String content;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private Long viewCount;
    private Integer commentCount;   // 暂时填 0，等评论做完再查

    public static PostDetail of(Post post) {
        return new PostDetail(
                post.getId(),
                post.getTitle(),
                post.getSummary(),
                post.getCoverUrl(),
                post.getContent(),
                post.getPublishedAt(),
                post.getUpdatedAt(),
                post.getViewCount(),
                0
        );
    }
}