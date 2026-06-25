package com.meetingroom.manager.domain.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InquiryReplyQueryDto {
    private Long id;
    private String content;
    private InquiryReplyAuthorQueryDto author;
}
