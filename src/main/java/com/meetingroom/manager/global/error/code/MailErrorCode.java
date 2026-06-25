package com.meetingroom.manager.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MailErrorCode implements ErrorCode {
    MAIL_SEND_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL_001", "이메일 전송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}