package com.meetingroom.manager.domain.user.exception;

import com.meetingroom.manager.global.error.code.UserErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class UserManagerException extends BusinessManagerException {
    public UserManagerException(UserErrorCode errorCode) {
        super(errorCode);
    }
}
