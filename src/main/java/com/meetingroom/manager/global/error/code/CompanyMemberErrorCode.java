package com.meetingroom.manager.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CompanyMemberErrorCode implements ErrorCode {
    COMPANY_MEMBER_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "COMPANY_MEMBER-01", "해당 회사의 회사 멤버가 아닙니다."),
    COMPANY_MEMBER_NOT_ADMIN(HttpStatus.FORBIDDEN, "COMPANY_MEMBER-02", "해당 회사의 회사 관리자가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

