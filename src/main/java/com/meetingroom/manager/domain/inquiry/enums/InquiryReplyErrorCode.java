package com.meetingroom.manager.domain.inquiry.enums;

import com.meetingroom.manager.global.error.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum InquiryReplyErrorCode implements ErrorCode {
    INQUIRY_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "INQUIRY_REPLY-01", "답변할 문의글을 찾을 수 없습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
