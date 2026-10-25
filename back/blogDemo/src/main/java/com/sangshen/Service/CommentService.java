package com.sangshen.Service;
import com.sangshen.entity.dto.CommentCreateRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface CommentService {
    void applyComment(CommentCreateRequest commentCreateRequest, String ip, String userAgent);
}
