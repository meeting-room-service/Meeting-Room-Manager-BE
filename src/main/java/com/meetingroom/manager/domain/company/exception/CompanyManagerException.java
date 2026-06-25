package com.meetingroom.manager.domain.company.exception;

import com.meetingroom.manager.global.error.code.CompanyErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class CompanyManagerException extends BusinessManagerException {
    public CompanyManagerException(CompanyErrorCode errorCode) {
        super(errorCode);
    }
}

