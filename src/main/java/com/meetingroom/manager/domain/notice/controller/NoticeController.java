package com.meetingroom.manager.domain.notice.controller;

import com.meetingroom.manager.domain.notice.dto.NoticeDto;
import com.meetingroom.manager.domain.notice.dto.NoticeListDto;
import com.meetingroom.manager.domain.notice.dto.NoticeListSearchRequestDto;
import com.meetingroom.manager.domain.notice.service.NoticeService;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.global.common.annotation.LoginUser;
import com.meetingroom.manager.global.common.response.ApiResponse;
import com.meetingroom.manager.global.common.response.OffsetPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    // 공지사항 상세 조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeDto>> getNotice(@LoginUser User currentUser, @PathVariable Long noticeId) {
        return ResponseEntity.ok(ApiResponse.success(noticeService.getNotice(currentUser, noticeId)));
    }

    // 공지사항 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<OffsetPageResponseDto<NoticeListDto>>> getNotices(
            @LoginUser User currentUser,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            NoticeListSearchRequestDto noticeListSearchRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(noticeService.getNotices(currentUser, pageable, noticeListSearchRequestDto)));
    }
}
