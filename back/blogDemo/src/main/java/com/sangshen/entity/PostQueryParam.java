package com.sangshen.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostQueryParam extends PageQueryParam {
    private String keyword;
}