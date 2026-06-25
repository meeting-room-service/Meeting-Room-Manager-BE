package com.meetingroom.manager.domain.user.dto;

import com.meetingroom.manager.domain.company.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoUpdateResponseDto {
    private Long id;
    private String loginId;
    private String name;
    private String email;
    private UserCompanyDto company;
    private Role role;
    private boolean certificated;
    private boolean changed;

    @Builder
    private UserInfoUpdateResponseDto(
            Long id,
            String loginId,
            String name,
            String email,
            UserCompanyDto company,
            Role role,
            boolean certificated,
            boolean changed
    ) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.email = email;
        this.company = company;
        this.role = role;
        this.certificated = certificated;
        this.changed = changed;
    }
}
