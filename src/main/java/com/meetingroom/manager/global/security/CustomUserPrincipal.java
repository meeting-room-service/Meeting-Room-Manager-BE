package com.meetingroom.manager.global.security;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CustomUserPrincipal {
    private Long id;

    @Builder
    private CustomUserPrincipal(Long id) {
        this.id = id;
    }
}
