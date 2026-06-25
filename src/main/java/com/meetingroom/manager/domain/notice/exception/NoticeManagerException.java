package com.meetingroom.manager.domain.notice.exception;

import com.meetingroom.manager.global.error.code.NoticeErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class NoticeManagerException extends BusinessManagerException {
    public NoticeManagerException(NoticeErrorCode errorCode) {
        super(errorCode);
    }
}

