package com.sangshen.entity.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminPostStatusRequest {
    @NotBlank(message = "文章状态不得为空")
    private String status;
}
