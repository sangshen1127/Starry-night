package com.sangshen.entity.admin;

import com.sangshen.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminPostItem {
    private  Long id;
    private String title;
    private String status;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private Long viewCount;


    public static AdminPostItem of(Post post){
        return new AdminPostItem(post.getId(), post.getTitle(), post.getStatus(), post.getPublishedAt(), post.getUpdatedAt(), post.getViewCount());
    }
}
