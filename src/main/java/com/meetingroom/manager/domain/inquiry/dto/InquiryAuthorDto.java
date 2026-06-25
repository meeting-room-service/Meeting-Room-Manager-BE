package com.meetingroom.manager.domain.inquiry.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class InquiryAuthorDto {
    private Long id;
    private String nickname;

    @Builder
    private InquiryAuthorDto(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
