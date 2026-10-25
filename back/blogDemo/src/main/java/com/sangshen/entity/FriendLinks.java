package com.sangshen.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("friend-links")
public class FriendLinks {
    private Long id;
    private String name;
    private String url;
    private String avatarUrl;
    private String description; //友链介绍
    private Integer sortOrder; //排序,越小越靠前
    private Integer visible; //是否可见,1可见，0隐藏
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
