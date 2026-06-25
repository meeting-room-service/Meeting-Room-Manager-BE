package com.meetingroom.manager.domain.notice.repository;

import com.meetingroom.manager.domain.notice.dto.NoticeListSearchRequestDto;
import com.meetingroom.manager.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {
    Page<Notice> getNotices(NoticeListSearchRequestDto noticeListSearchRequestDto, Pageable pageable);
}
