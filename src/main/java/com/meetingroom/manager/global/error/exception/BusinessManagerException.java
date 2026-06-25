package com.meetingroom.manager.global.error.exception;

import com.meetingroom.manager.global.error.code.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessManagerException extends MeetingRoomManagerException {
    protected BusinessManagerException(ErrorCode errorCode) {
        super(errorCode);
    }
}