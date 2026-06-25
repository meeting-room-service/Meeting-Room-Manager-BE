package com.meetingroom.manager.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailVerificationSendResponseDto {
    private long expiresInSeconds;

    @Builder
    private EmailVerificationSendResponseDto(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }
}
