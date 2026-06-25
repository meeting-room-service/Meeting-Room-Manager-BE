package com.meetingroom.manager.domain.notice.service;

import com.meetingroom.manager.domain.notice.dto.NoticeDto;
import com.meetingroom.manager.domain.notice.dto.NoticeListDto;
import com.meetingroom.manager.domain.notice.dto.NoticeListSearchRequestDto;
import com.meetingroom.manager.domain.notice.entity.Notice;
import com.meetingroom.manager.domain.notice.exception.NoticeManagerException;
import com.meetingroom.manager.domain.notice.mapper.NoticeMapper;
import com.meetingroom.manager.domain.notice.repository.NoticeRepository;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.domain.user.exception.UserManagerException;
import com.meetingroom.manager.global.common.response.OffsetPageResponseDto;
import com.meetingroom.manager.global.error.code.NoticeErrorCode;
import com.meetingroom.manager.global.error.code.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeMapper noticeMapper;

    // 공지사항 상세 조회
    @Transactional
    public NoticeDto getNotice(User currentUser,  Long noticeId) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 공지사항 조회 (카테고리 정보 포함)
        Notice notice = noticeRepository.findByIdWithCategory(noticeId)
                .orElseThrow(() -> new NoticeManagerException(NoticeErrorCode.NOTICE_NOT_FOUND));

        NoticeDto noticeDto = noticeMapper.toDto(notice);

        notice.increaseViewCount();

        return noticeDto;
    }

    // 공지사항 목록 조회
    public OffsetPageResponseDto<NoticeListDto> getNotices(User currentUser, Pageable pageable, NoticeListSearchRequestDto noticeListSearchRequestDto) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 공지사항 목록 조회 (카테고리 정보 포함)
        Page<Notice> page = noticeRepository.getNotices(noticeListSearchRequestDto, pageable);

        List<NoticeListDto> items = page.getContent().stream()
                .map(noticeMapper::toListDto)
                .toList();

        return OffsetPageResponseDto.<NoticeListDto>builder()
                .totalCount(page.getTotalElements())
                .page(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasPrevious(page.hasPrevious())
                .hasNext(page.hasNext())
                .content(items)
                .build();
    }
}
