package com.meetingroom.manager.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserCompanyDto {
    private Long id;
    private String name;

    @Builder
    private UserCompanyDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
