package com.meetingroom.manager.domain.inquiry.controller;

import jakarta.validation.Valid;
import com.meetingroom.manager.domain.inquiry.dto.InquiryCreateRequestDto;
import com.meetingroom.manager.domain.inquiry.dto.InquiryUpdateRequestDto;
import com.meetingroom.manager.domain.inquiry.dto.InquiryDto;
import com.meetingroom.manager.domain.inquiry.dto.InquiryListDto;
import com.meetingroom.manager.domain.inquiry.dto.InquiryListSearchRequestDto;
import com.meetingroom.manager.domain.inquiry.service.InquiryService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;

    // 문의 등록
    @PostMapping
    public ResponseEntity<ApiResponse<InquiryDto>> createInquiry(
            @LoginUser User currentUser,
            @RequestPart("request") @Valid InquiryCreateRequestDto inquiryCreateRequestDto,
            @RequestPart(value = "inquiryImageFiles", required = false) List<MultipartFile> inquiryImageFiles
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(inquiryService.createInquiry(currentUser, inquiryCreateRequestDto, inquiryImageFiles)));
    }

    // 문의 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InquiryDto>> getInquiry(
            @LoginUser User currentUser,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                inquiryService.getInquiry(currentUser, id)));
    }

    // 문의 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<OffsetPageResponseDto<InquiryListDto>>> getInquiries(
            @LoginUser User currentUser,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            InquiryListSearchRequestDto inquiryListSearchRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                inquiryService.getInquiries(currentUser, pageable, inquiryListSearchRequestDto)));
    }

    // 문의 수정
    @PatchMapping(value = "/{inquiryId}")
    public ResponseEntity<ApiResponse<InquiryDto>> updateInquiry(
            @LoginUser User currentUser,
            @PathVariable Long inquiryId,
            @RequestPart("request") @Valid InquiryUpdateRequestDto inquiryUpdateRequestDto,
            @RequestPart(value = "inquiryImageFiles", required = false) List<MultipartFile> inquiryImageFiles
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                inquiryService.updateInquiry(currentUser, inquiryId, inquiryUpdateRequestDto, inquiryImageFiles)));
    }

    // 문의 삭제
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<Void> deleteInquiry(
            @LoginUser User currentUser,
            @PathVariable Long inquiryId
    ) {
        inquiryService.deleteInquiry(currentUser, inquiryId);
        return ResponseEntity.noContent().build();
    }
}
