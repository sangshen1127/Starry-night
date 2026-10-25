package com.sangshen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("comments")
public class Comment {
    private Long id;
    private Long postId;
    private String nickname;
    private String email;
    private String website;
    private String content;
    private String status;
    private String ip;  // 兼容ip
    private String userAgent;   //浏览器标识，防灌水
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
