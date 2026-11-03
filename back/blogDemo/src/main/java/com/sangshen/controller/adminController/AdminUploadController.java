package com.sangshen.controller.adminController;
import com.sangshen.service.admin.UploadService;
import com.sangshen.entity.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminUploadController {
    private final UploadService uploadService;

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file){
        return Result.success(uploadService.upload(file));
    }
}
