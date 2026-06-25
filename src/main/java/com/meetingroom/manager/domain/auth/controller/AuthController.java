package com.meetingroom.manager.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import com.meetingroom.manager.domain.auth.dto.AvailabilityResponseDto;
import com.meetingroom.manager.domain.auth.dto.EmailAvailabilityRequestDto;
import com.meetingroom.manager.domain.auth.dto.LoginIdAvailabilityRequestDto;
import com.meetingroom.manager.domain.auth.dto.NicknameAvailabilityRequestDto;
import com.meetingroom.manager.domain.auth.service.AuthService;
import com.meetingroom.manager.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 엑세스 토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        authService.refreshAccessToken(request, response);
        return ResponseEntity.noContent().build();
    }

    // 로그아웃
    @PostMapping("/sign-out")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.noContent().build();
    }

    // 로그인 아이디 중복 체크
    @PostMapping("/login-id/availability")
    public ResponseEntity<ApiResponse<AvailabilityResponseDto>> loginIdAvailability(
            @RequestBody @Valid LoginIdAvailabilityRequestDto loginIdAvailabilityRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(authService.loginIdAvailability(loginIdAvailabilityRequestDto)));
    }

    // 닉네임 중복 체크
    @PostMapping("/nickname/availability")
    public ResponseEntity<ApiResponse<AvailabilityResponseDto>> nicknameAvailability(
            @RequestBody @Valid NicknameAvailabilityRequestDto nicknameAvailabilityRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(authService.nicknameAvailability(nicknameAvailabilityRequestDto)));
    }

    // 이메일 중복 체크
    @PostMapping("/email/availability")
    public ResponseEntity<ApiResponse<AvailabilityResponseDto>> emailAvailability(
            @RequestBody @Valid EmailAvailabilityRequestDto emailAvailabilityRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(authService.emailAvailability(emailAvailabilityRequestDto)));
    }
}