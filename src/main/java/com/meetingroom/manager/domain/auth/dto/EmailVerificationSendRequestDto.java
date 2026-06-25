package com.meetingroom.manager.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.meetingroom.manager.domain.auth.enums.EmailVerificationPurpose;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailVerificationSendRequestDto {
    private EmailVerificationPurpose purpose;

    @NotBlank
    @Email
    private String email;

    @Builder
    private EmailVerificationSendRequestDto(EmailVerificationPurpose purpose, String email) {
        this.purpose = purpose;
        this.email = email;
    }
}

