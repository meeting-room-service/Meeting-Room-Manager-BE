package com.meetingroom.manager.domain.room.exception;

import com.meetingroom.manager.global.error.code.RoomErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class RoomManagerException extends BusinessManagerException {
    public RoomManagerException(RoomErrorCode errorCode) {
        super(errorCode);
    }
}

