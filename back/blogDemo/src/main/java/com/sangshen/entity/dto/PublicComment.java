package com.sangshen.entity.dto;

import com.sangshen.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PublicComment {
    private Long id;
    private String nickname;
    private String website;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static PublicComment of(Comment comment){
        return new PublicComment(comment.getId(), comment.getNickname(), comment.getWebsite(), comment.getContent(), comment.getCreatedAt());
    }
}
