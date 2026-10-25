package com.sangshen.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)    // 继承父类的equals和hashCode方法,比较我自己的字段之前，先让父类比它的字段
public class CommentQueryParam extends PageQueryParam {
    private Long id;
}