package com.meetingroom.manager.global.error.exception;

import com.meetingroom.manager.global.error.code.ErrorCode;
import lombok.Getter;

@Getter
public class MeetingRoomManagerException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;

    public MeetingRoomManagerException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
}
