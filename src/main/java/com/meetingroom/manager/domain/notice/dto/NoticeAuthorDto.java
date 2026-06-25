package com.meetingroom.manager.domain.notice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NoticeAuthorDto {
    private String id;
    private String nickname;

    @Builder
    private NoticeAuthorDto(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
