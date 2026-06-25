package com.meetingroom.manager.domain.auth.exception;

import com.meetingroom.manager.global.error.code.AuthErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class AuthManagerException extends BusinessManagerException {
    public AuthManagerException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}

