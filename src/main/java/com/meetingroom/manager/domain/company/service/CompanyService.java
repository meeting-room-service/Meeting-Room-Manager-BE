package com.meetingroom.manager.domain.company.service;

import com.meetingroom.manager.domain.company.dto.CompanyDto;
import com.meetingroom.manager.domain.company.dto.CompanyMemberListDto;
import com.meetingroom.manager.domain.company.dto.CopanyMemberUserDto;
import com.meetingroom.manager.domain.company.exception.CompanyManagerException;
import com.meetingroom.manager.domain.company.mapper.CompanyMapper;
import com.meetingroom.manager.domain.company.repository.CompanyMemberRepository;
import com.meetingroom.manager.domain.company.repository.CompanyRepository;
import com.meetingroom.manager.global.error.code.CompanyErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final CompanyMapper companyMapper;

    // 회사 상세 조회
    public CompanyDto getCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .map(companyMapper::toDto)
                .orElseThrow(() -> new CompanyManagerException(CompanyErrorCode.COMPANY_NOT_FOUND));
    }

    // 회사 멤버 목록 전체 조회
    public List<CompanyMemberListDto> getAllCompanyMembers(Long companyId) {
        return companyMemberRepository.findAllByCompany_Id(companyId).stream()
                .map(companyMember -> CompanyMemberListDto.builder()
                        .id(companyMember.getId())
                        .user(CopanyMemberUserDto.builder()
                                .id(companyMember.getUser().getId())
                                .nickname(companyMember.getUser().getNickname())
                                .build())
                        .role(companyMember.getRole())
                        .build())
                .toList();
    }
}
