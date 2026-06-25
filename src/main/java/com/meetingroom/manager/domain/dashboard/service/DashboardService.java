package com.meetingroom.manager.domain.dashboard.service;
import com.meetingroom.manager.domain.company.entity.CompanyMember;
import com.meetingroom.manager.domain.company.exception.CompanyMemberManagerException;
import com.meetingroom.manager.domain.company.repository.CompanyMemberRepository;
import com.meetingroom.manager.domain.dashboard.dto.DashboardDto;
import com.meetingroom.manager.domain.dashboard.dto.DashboardQueryDto;
import com.meetingroom.manager.domain.dashboard.mapper.DashboardMapper;
import com.meetingroom.manager.domain.dashboard.repository.DashboardRepository;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.global.error.code.CompanyMemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardRepository dashboardRepository;
    private final CompanyMemberRepository  companyMemberRepository;
    private final DashboardMapper dashboardMapper;

    // 대시보드 조회
    public DashboardDto getDashboard(User currentUser, Long companyId) {
        validateCurrentUserBelongsToCompany(currentUser, companyId);

        List<DashboardQueryDto> rows = dashboardRepository.getDashboard(companyId, LocalDateTime.now());
        return dashboardMapper.toDto(rows);
    }

    // 현재 사용자 회사 소속 확인
    private void validateCurrentUserBelongsToCompany(User currentUser, Long companyId) {
        Optional<CompanyMember> companyMember = companyMemberRepository.findByUser_IdAndCompany_Id(currentUser.getId(), companyId);

        if (companyMember.isEmpty()) {
            throw new CompanyMemberManagerException(CompanyMemberErrorCode.COMPANY_MEMBER_NOT_AUTHORIZED);
        }
    }
}
