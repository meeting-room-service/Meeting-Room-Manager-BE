package com.meetingroom.manager.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailVerificationVerifyResponseDto {
    private String verificationId;

    @Builder
    private EmailVerificationVerifyResponseDto(String verificationId) {
        this.verificationId = verificationId;
    }
}
