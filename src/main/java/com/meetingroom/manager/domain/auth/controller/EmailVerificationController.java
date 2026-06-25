package com.meetingroom.manager.domain.auth.controller;

import jakarta.validation.Valid;
import com.meetingroom.manager.domain.auth.dto.EmailVerificationSendRequestDto;
import com.meetingroom.manager.domain.auth.dto.EmailVerificationSendResponseDto;
import com.meetingroom.manager.domain.auth.dto.EmailVerificationVerifyRequestDto;
import com.meetingroom.manager.domain.auth.dto.EmailVerificationVerifyResponseDto;
import com.meetingroom.manager.domain.auth.service.EmailVerificationService;
import com.meetingroom.manager.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/email-verification")
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    // 이메일 인증 코드 발송
    @PostMapping("/sendCode")
    public ResponseEntity<ApiResponse<EmailVerificationSendResponseDto>> sendCode(@RequestBody @Valid EmailVerificationSendRequestDto emailVerificationSendRequestDto) {
        return ResponseEntity.ok(ApiResponse.success(emailVerificationService.sendCode(emailVerificationSendRequestDto)));
    }

    // 이메일 인증 코드 검증
    @PostMapping("/verifyCode")
    public ResponseEntity<ApiResponse<EmailVerificationVerifyResponseDto>> verifyCode(@RequestBody @Valid EmailVerificationVerifyRequestDto emailVerificationVerifyRequestDto) {
        return ResponseEntity.ok(ApiResponse.success(emailVerificationService.verifyCode(emailVerificationVerifyRequestDto)));
    }
}

