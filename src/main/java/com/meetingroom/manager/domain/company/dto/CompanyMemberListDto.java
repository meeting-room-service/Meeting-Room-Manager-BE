package com.meetingroom.manager.domain.company.dto;

import com.meetingroom.manager.domain.company.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CompanyMemberListDto {
    private Long id;
    private CopanyMemberUserDto user;
    private Role role;

    @Builder
    private CompanyMemberListDto(Long id, CopanyMemberUserDto user, Role role) {
        this.id = id;
        this.user = user;
        this.role = role;
    }
}
