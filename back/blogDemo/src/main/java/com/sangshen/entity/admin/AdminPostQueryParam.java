package com.sangshen.entity.admin;

import com.sangshen.entity.PageQueryParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminPostQueryParam extends PageQueryParam {
    private String status;
    private String keyword; //文章模糊搜索
}
