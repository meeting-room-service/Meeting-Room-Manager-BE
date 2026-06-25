package com.meetingroom.manager.domain.company.controller;

import com.meetingroom.manager.domain.company.dto.CompanyDto;
import com.meetingroom.manager.domain.company.service.CompanyService;
import com.meetingroom.manager.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    // 회사 상세 조회
    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyDto>> getCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(ApiResponse.success(companyService.getCompany(companyId)));
    }
}
