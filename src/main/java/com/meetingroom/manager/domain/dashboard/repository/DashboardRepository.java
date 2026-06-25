package com.meetingroom.manager.domain.dashboard.repository;

import com.meetingroom.manager.domain.dashboard.dto.DashboardQueryDto;

import java.time.LocalDateTime;
import java.util.List;

public interface DashboardRepository {
    List<DashboardQueryDto> getDashboard(Long companyId, LocalDateTime at);
}
