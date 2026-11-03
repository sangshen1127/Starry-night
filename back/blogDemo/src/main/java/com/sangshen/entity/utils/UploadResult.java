package com.sangshen.entity.utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UploadResult {
    private String url;
    private String name;
    private Long size;
}
