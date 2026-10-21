package com.sangshen.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostQueryParam {
    private Integer page = 1;
    private Integer size = 10;
    private String keyword;
}
