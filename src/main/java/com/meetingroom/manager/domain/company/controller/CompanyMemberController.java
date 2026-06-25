package com.meetingroom.manager.domain.company.controller;

import com.meetingroom.manager.domain.company.dto.CompanyMemberListDto;
import com.meetingroom.manager.domain.company.service.CompanyService;
import com.meetingroom.manager.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/companies/{companyId}/members")
@RequiredArgsConstructor
public class CompanyMemberController {
    private final CompanyService companyService;

    // 회사 멤버 목록 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyMemberListDto>>> getAllCompanyMembers(@PathVariable Long companyId) {
        return ResponseEntity.ok(ApiResponse.success(companyService.getAllCompanyMembers(companyId)));
    }
}
