package com.sangshen.entity.admin;
import com.sangshen.entity.PageQueryParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminCommentQueryParam extends PageQueryParam {
    private String status;
    private Long postId;
}
