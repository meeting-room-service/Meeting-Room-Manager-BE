package com.meetingroom.manager.domain.auth.exception;

import com.meetingroom.manager.global.error.code.ErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class EmailVerificationManagerException extends BusinessManagerException {
    public EmailVerificationManagerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
