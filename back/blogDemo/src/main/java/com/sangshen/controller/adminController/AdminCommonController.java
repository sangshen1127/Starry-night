package com.sangshen.controller.adminController;

import com.sangshen.service.admin.AdminCommonService;
import com.sangshen.entity.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminCommonController {
    private final AdminCommonService adminCommonService;
    @GetMapping("/stats")
    public Result showAllCount(){
        return Result.success(adminCommonService.showAllCounts());
    }
}
