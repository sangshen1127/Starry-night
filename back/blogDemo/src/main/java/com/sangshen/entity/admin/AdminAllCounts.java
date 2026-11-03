package com.sangshen.entity.admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdminAllCounts {
    private Long postCount;
    private Long draftCount;
    private Long commentCount;
    private Long pendingCommentCount;
    private Long friendLinkCount;
}
