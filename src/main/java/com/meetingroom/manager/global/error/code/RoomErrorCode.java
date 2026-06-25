package com.meetingroom.manager.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RoomErrorCode implements ErrorCode {
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "ROOM-01", "회의실을 찾을 수 없습니다."),
    ROOM_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "ROOM-02", "회의실 관리 권한이 없습니다."),
    ROOM_NAME_DUPLICATE(HttpStatus.BAD_REQUEST, "ROOM-03", "동일한 이름의 회의실이 이미 등록되어 있습니다."),
    ROOM_NOT_IN_COMPANY(HttpStatus.BAD_REQUEST, "ROOM-04", "회사가 소속된 회의실이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

