package com.sangshen.service.admin;

import com.sangshen.entity.utils.UploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    UploadResult upload(MultipartFile file);
}
