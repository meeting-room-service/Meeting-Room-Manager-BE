package com.meetingroom.manager.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.meetingroom.manager.domain.auth.enums.EmailVerificationPurpose;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailVerificationVerifyRequestDto {
    private EmailVerificationPurpose purpose;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{6}")
    private String code;

    @Builder
    private EmailVerificationVerifyRequestDto(EmailVerificationPurpose purpose, String email, String code) {
        this.purpose = purpose;
        this.email = email;
        this.code = code;
    }
}

