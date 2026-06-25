package com.meetingroom.manager.domain.user.contoller;

import jakarta.validation.Valid;
import com.meetingroom.manager.domain.user.dto.UserCreateRequestDto;
import com.meetingroom.manager.domain.user.dto.UserDto;
import com.meetingroom.manager.domain.user.dto.UserProfileDto;
import com.meetingroom.manager.domain.user.dto.UserProfileUpdateResponseDto;
import com.meetingroom.manager.domain.user.dto.UserInfoUpdateResponseDto;
import com.meetingroom.manager.domain.user.dto.UserUpdateRequestDto;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.domain.user.dto.UserProfileUpdateRequestDto;
import com.meetingroom.manager.domain.user.service.UserService;
import com.meetingroom.manager.global.common.annotation.LoginUser;
import com.meetingroom.manager.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createUser(
            @RequestPart("request") @Valid UserCreateRequestDto userCreateRequestDto,
            @RequestPart("employmentCertificateFile") MultipartFile employmentCertificateFile
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userService.join(userCreateRequestDto, employmentCertificateFile)));
    }

    // 사용자 정보 조회
    @GetMapping
    public ResponseEntity<ApiResponse<UserDto>> getUserInfo(@LoginUser User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserInfo(currentUser)));
    }

    // 사용자 정보 수정
    @PatchMapping
    public ResponseEntity<ApiResponse<UserInfoUpdateResponseDto>> updateUserInfo(@LoginUser User currentUser, @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUserInfo(currentUser, userUpdateRequestDto)));
    }

    // 사용자 프로필 조회
    @GetMapping("/profiles")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserProfile(@LoginUser User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserProfile(currentUser)));
    }

    // 사용자 프로필 수정
    @PatchMapping("/profiles")
    public ResponseEntity<ApiResponse<UserProfileUpdateResponseDto>> updateUserProfile(@LoginUser User currentUser,
                                                                                        @RequestPart("request") @Valid
                                                                                        UserProfileUpdateRequestDto userProfileUpdateRequestDto,
                                                                                        @RequestPart(value = "profileImageFile", required = false) MultipartFile profileImageFile) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUserProfile(currentUser, userProfileUpdateRequestDto, profileImageFile)));
    }
}
