package com.meetingroom.manager.domain.inspection.controller;

import com.meetingroom.manager.domain.inspection.dto.*;
import jakarta.validation.Valid;
import com.meetingroom.manager.domain.inspection.service.InspectionService;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.global.common.annotation.LoginUser;
import com.meetingroom.manager.global.common.response.ApiResponse;
import com.meetingroom.manager.global.common.response.OffsetPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company/{companyId}/inspections")
@RequiredArgsConstructor
public class InspectionController {
    private final InspectionService inspectionService;

    // 점검 등록
    @PostMapping
    public ResponseEntity<ApiResponse<InspectionDto>> createInspection(
            @LoginUser User currentUser,
            @PathVariable Long companyId,
            @Valid @RequestBody InspectionCreateRequestDto inspectionCreateRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                inspectionService.createInspection(currentUser, companyId, inspectionCreateRequestDto)));
    }

    // 점검 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<OffsetPageResponseDto<InspectionListDto>>> getInspections(
            @LoginUser User currentUser,
            @PathVariable Long companyId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @Valid @ModelAttribute InspectionListSearchRequestDto inspectionListDto

    ) {
        return ResponseEntity.ok(ApiResponse.success(
                inspectionService.getInspections(currentUser, companyId, pageable, inspectionListDto)));
    }

    // 점검 수정
    @PatchMapping("/{inspectionId}")
    public ResponseEntity<ApiResponse<InspectionDto>> updateInspection(
            @LoginUser User currentUser,
            @PathVariable Long companyId,
            @PathVariable Long inspectionId,
            @Valid @RequestBody InspectionUpdateRequestDto inspectionUpdateRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                inspectionService.updateInspection(currentUser, companyId, inspectionId, inspectionUpdateRequestDto)));
    }

    // 점검 삭제
    @DeleteMapping("/{inspectionId}")
    public ResponseEntity<Void> deleteInspection(
            @LoginUser User currentUser,
            @PathVariable Long companyId,
            @PathVariable Long inspectionId
    ) {
        inspectionService.deleteInspection(currentUser, companyId, inspectionId);
        return ResponseEntity.noContent().build();
    }
}
