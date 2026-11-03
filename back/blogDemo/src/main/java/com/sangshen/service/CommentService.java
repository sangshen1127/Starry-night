package com.sangshen.service;
import com.sangshen.entity.dto.CommentCreateRequest;

public interface CommentService {
    void applyComment(CommentCreateRequest commentCreateRequest, String ip, String userAgent);
}
