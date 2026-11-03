package com.sangshen.entity.admin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminAddPostItem {
    @NotBlank(message = "文章标题不得为空")
    private String title;
    private String summary;
    private String coverUrl;
    @NotBlank(message = "文章内容不得为空")
    private String content;
    private String status = "draft";    //文章默认为草稿
}
