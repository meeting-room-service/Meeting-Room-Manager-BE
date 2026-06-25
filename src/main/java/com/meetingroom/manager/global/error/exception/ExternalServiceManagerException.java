package com.meetingroom.manager.global.error.exception;

import com.meetingroom.manager.global.error.code.ErrorCode;

public class ExternalServiceManagerException extends MeetingRoomManagerException {
    public ExternalServiceManagerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
