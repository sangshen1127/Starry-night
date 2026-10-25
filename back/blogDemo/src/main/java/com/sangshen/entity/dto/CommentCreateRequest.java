package com.sangshen.entity.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateRequest {
    @NotNull(message = "文章 id 不能为空")
    private Long postId;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 32, message = "昵称不能超过 32 个字符")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Size(max = 128)
    private String email;

    @Size(max = 255)
    private String website;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过 500 个字符")
    private String content;
}