package com.sangshen.entity.admin;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminCommentItem {
    private Long id;
    private Long postId;
    private String postTitle;
    private String nickname;
    private String email;
    private String website;
    private String content;
    private String status;
    private String ip;  //获取当前用户ip地址
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; //评论创建时间
}
