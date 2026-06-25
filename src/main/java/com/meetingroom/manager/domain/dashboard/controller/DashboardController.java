package com.meetingroom.manager.domain.dashboard.controller;

import com.meetingroom.manager.domain.dashboard.dto.DashboardDto;
import com.meetingroom.manager.domain.dashboard.service.DashboardService;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.global.common.annotation.LoginUser;
import com.meetingroom.manager.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company/{companyId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    // 대시보드 조회
    @GetMapping
    public ApiResponse<DashboardDto> getDashboard(
            @LoginUser User currentUser,
            @PathVariable Long companyId
    ) {
        return ApiResponse.success(dashboardService.getDashboard(currentUser, companyId));
    }
}
