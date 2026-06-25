package com.meetingroom.manager.domain.company.exception;

import com.meetingroom.manager.global.error.code.CompanyMemberErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class CompanyMemberManagerException extends BusinessManagerException {
    public CompanyMemberManagerException(CompanyMemberErrorCode errorCode) {
        super(errorCode);
    }
}

