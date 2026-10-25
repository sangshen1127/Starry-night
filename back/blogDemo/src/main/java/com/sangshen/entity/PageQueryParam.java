package com.sangshen.entity;

import lombok.Data;

@Data
public abstract class PageQueryParam {

    private Integer page = 1;
    private Integer size = 10;

    /**
     * 补默认值 + 校验范围，子类在 Controller 里调一次即可
     */
    public void normalize() {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
        if (page < 1) {
            throw new IllegalArgumentException("页码不能够小于1");
        }
        if (size < 1 || size > 50) {
            throw new IllegalArgumentException("每页数量不能够小于1或者大于50");
        }
    }
}