package com.meetingroom.manager.domain.inquiry.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class InquiryReplyDto {
    private String content;
    private InquiryReplyAuthorDto author;

    @Builder
    private InquiryReplyDto(String content, InquiryReplyAuthorDto author) {
        this.content = content;
        this.author = author;
    }
}
